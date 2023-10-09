package alexey.tools.torrent.core

import alexey.tools.common.collections.IndexedObject
import alexey.tools.common.collections.IndexedObjectCollection
import alexey.tools.common.collections.IntList
import alexey.tools.common.collections.forEachInt
import com.frostwire.jlibtorrent.Priority
import com.frostwire.jlibtorrent.TorrentHandle
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock

class TorrentData(val handle: TorrentHandle): Closeable {

    private val files = handle.torrentFile().files()
    private val pieceLength = files.pieceLength().toLong()
    private val entries: Array<TorrentEntryData?>
    private val requests = IntList()
    private var closed = false

    val savePath = File(handle.savePath())



    init {
        val entryCount = files.numFiles()
        entries = arrayOfNulls(entryCount)
        handle.prioritizeFiles(Array(entryCount){ Priority.IGNORE })
    }



    fun obtain(index: Int): TorrentEntryData {
        synchronized(entries) {
            if (closed) throw IllegalStateException("Torrent is closed!")
            var entry = entries[index]
            if (entry != null) return entry
            entry = TorrentEntryData(index)
            entries[index] = entry
            return entry
        }
    }

    fun pieceFinished(index: Int) {
        while (!handle.havePiece(index)) Thread.sleep(1)
        findEntry(index).pieceFinished(index)
    }

    fun doRequests() {
        var deadline = 0
        synchronized(requests) {
            requests.forEachInt { handle.setPieceDeadline(it, deadline++) }
            requests.clear()
        }
    }

    fun close0() {
        synchronized(entries) {
            if (closed) return
            for (entry in entries) entry?.close0()
            closed = true
        }
        savePath.deleteRecursively()
    }

    override fun close() {
        synchronized(entries) {
            if (closed) return
            for (entry in entries) entry?.close()
            closed = true
        }
    }



    private fun getPiece(bytes: Long) = (bytes / pieceLength).toInt()

    private fun findEntry(index: Int): TorrentEntryData {
        synchronized(entries) {
            for (entry in entries) if (entry != null && !entry.outOfBonds(index)) return entry
        }
        throw IllegalStateException("Sync torrent entry not found (index = $index)!")
    }



    inner class TorrentEntryData(val index: Int): Closeable {

        val file = File(savePath, files.filePath(index))

        private val firstByte = files.fileOffset(index)
        private val lastByte = firstByte + files.fileSize(index)

        private val firstPiece = getPiece(firstByte)
        private val lastPiece = getPiece(lastByte - 1L)

        private val streams = IndexedObjectCollection<EntryInputStream>(2)
        private var state = NEW



        fun getInputStream(): InputStream {
            synchronized(streams) {
                when(state) {
                    CLOSED -> throw IllegalStateException("Entry is closed!")
                    NEW -> {
                        if (!file.exists()) {
                            file.parentFile.mkdirs()
                            file.createNewFile()
                        }
                        enable()
                    }
                }
                return EntryInputStream().also { streams.add(it) }
            }
        }

        fun pieceFinished(index: Int) {
            synchronized(streams) {
                streams.forEach { it.pieceFinished(index) }
            }
        }

        fun outOfBonds(index: Int) = index > lastPiece

        fun disable() {
            synchronized(streams) {
                if (streams.isNotEmpty) return
                handle.filePriority(index, Priority.IGNORE)
                state = NEW
            }
        }

        fun close0() {
            synchronized(streams) {
                if (state == CLOSED) return
                while (streams.isNotEmpty) streams.removeLast().apply { close0(); index = -1 }
                state = CLOSED
            }
        }

        override fun close() {
            synchronized(streams) {
                if (state == CLOSED) return
                while (streams.isNotEmpty) streams.removeLast().apply { close0(); index = -1 }
                handle.filePriority(index, Priority.IGNORE)
                state = CLOSED
            }
        }



        private fun enable() {
            handle.filePriority(index, Priority.NORMAL)
            handle.resume()
            state = INITIALIZED
        }



        private inner class EntryInputStream: InputStream(), IndexedObject {

            private val data = file.inputStream()

            private var currentByte = firstByte
            private var currentPiece = firstPiece
            private var index = -1

            private val lock = ReentrantLock()
            private val pieceFinished = lock.newCondition()
            private var closed = false



            override fun read(b: ByteArray, off: Int, len: Int): Int {
                lock.lock()
                try {
                    if (len < 1) return 0
                    if (isBad()) return -1
                    currentByte += len
                    val endPiece = getPiece(currentByte - 1L)
                    while (currentPiece <= endPiece) {
                        obtainPiece(currentPiece)
                        if (closed) return -1
                        currentPiece++
                    }
                    currentPiece = getPiece(currentByte)
                    return data.read(b, off, len)
                } finally {
                    lock.unlock()
                }
            }

            override fun available(): Int {
                lock.lock()
                if (isBad()) { lock.unlock(); return 0 }
                var piece = currentPiece
                while (handle.havePiece(piece) && piece < lastPiece) piece++
                val delta = piece - currentPiece
                if (delta == 0) { lock.unlock(); return 0 }
                val dust = pieceLength - currentByte + currentPiece * pieceLength
                lock.unlock()
                val total = (delta - 1) * pieceLength + dust
                return if (total < Int.MAX_VALUE) total.toInt() else Int.MAX_VALUE
            }

            override fun skip(n: Long): Long {
                lock.lock()
                if (isBad()) { lock.unlock(); return 0 }
                try {
                    data.skip(n)
                    currentByte += n
                    currentPiece = getPiece(currentByte)
                } finally {
                    lock.unlock()
                }
                return n
            }

            override fun read(): Int {
                lock.lock()
                try {
                    if (isBad()) return -1
                    obtainPiece(currentPiece)
                    if (closed) return -1
                    currentByte++
                    currentPiece = getPiece(currentByte)
                    return data.read()
                } finally {
                    lock.unlock()
                }
            }

            override fun markSupported(): Boolean = false

            override fun close() {
                synchronized(streams) {
                    if (!streams.safeRemoveReference(this)) return
                }
                close0()
            }

            override fun getIndex() = index

            override fun setIndex(index: Int) { this.index = index }



            fun pieceFinished(index: Int) {
                lock.lock()
                if (currentPiece == index) {
                    if (index < lastPiece) handle.setPieceDeadline(index + 1, 0)
                    pieceFinished.signalAll()
                }
                lock.unlock()
            }

            fun close0() {
                lock.lock()
                closed = true
                pieceFinished.signalAll()
                lock.unlock()
                data.close()
            }



            private fun obtainPiece(index: Int) {
                if (handle.havePiece(currentPiece)) return
                synchronized(requests) { requests.add(index) }
                pieceFinished.await()
            }



            private fun isBad() = closed || currentByte >= lastByte
        }
    }



    companion object {
        private const val NEW: Byte         = 0
        private const val INITIALIZED: Byte = 1
        private const val CLOSED: Byte      = 2
    }
}