package alexey.tools.common.resources

import alexey.tools.common.concurrent.DefaultRunnableFuture
import alexey.tools.common.misc.MiscUtils
import alexey.tools.common.misc.newDaemonExecutor
import alexey.tools.common.misc.silentTry
import alexey.tools.common.misc.submitTask
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Future
import kotlin.math.ceil
import kotlin.math.min

class CachedInputStream(val resource: Resource,
                        val executor: Executor = newDaemonExecutor(),
                        val file: File = File("temp"),
                        val spread: Int = 2,
                        val pieceSize: Int = 1024 * 64): Closeable {

    val size = resource.length()

    private val cacheLock = MiscUtils.newObject()
    private val cache = RandomAccessFile(file, "rw")
    private val readTasks = arrayOfNulls<Future<*>>(ceil(size.toDouble() / pieceSize).toInt())
    private val lastPiece = readTasks.size - 1



    init { cache.setLength(size) }



    fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
        val from = getPiece(position)
        if (from > lastPiece) return -1
        try {
            cachePieces(from, getPiece(position + length))
        } catch (_: Throwable) {
            return -1
        }
        synchronized(cacheLock) {
            cache.seek(position)
            return cache.read(buffer, offset, length)
        }
    }

    fun read(position: Long): Int {
        val piece = getPiece(position)
        if (piece > lastPiece) return -1
        try {
            cachePieces(piece, piece)
        } catch (_: Throwable) {
            return -1
        }
        synchronized(cacheLock) {
            cache.seek(position)
            return cache.read()
        }
    }

    fun read(position: Long, buffer: ByteArray): Int =
        read(position, buffer, 0, buffer.size)

    fun getInputStream(): InputStream = Input()



    override fun close() {
        synchronized(readTasks) {
            readTasks.forEach { it?.cancel(true) }
            readTasks.forEach { if (it != null && it !== DefaultRunnableFuture.INSTANCE) silentTry { it.get() } }
        }
        cache.close()
        file.delete()
    }



    private fun getPiece(position: Long): Int {
        return (position / pieceSize).toInt()
    }

    private fun cachePieces(fromPiece: Int, toPiece: Int) {
        synchronized(readTasks) {
            for (i in fromPiece..min(toPiece + spread, lastPiece)) {
                if (readTasks[i] != null) continue
                readTasks[i] = executor.submitTask(ReadTask(i))
            }
            for (i in fromPiece..min(toPiece, lastPiece)) {
                val future = readTasks[i] ?: throw NullPointerException()
                if (future === DefaultRunnableFuture.INSTANCE) continue
                future.get()
                readTasks[i] = DefaultRunnableFuture.INSTANCE
            }
        }
    }



    private inner class Input: InputStream() {

        private val lock = MiscUtils.newObject()
        private var position = 0L



        override fun read(): Int {
            synchronized(lock) {
                val read = read(position)
                if (read == -1) return -1
                position++
                return read
            }
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            synchronized(lock) {
                val read = read(position, b, off, len)
                if (read == -1) return -1
                position += read
                return read
            }
        }

        override fun read(b: ByteArray): Int {
            return read(b, 0, b.size)
        }

        override fun skip(n: Long): Long {
            synchronized(lock) {
                val old = position
                position += n
                return if (position > size) {
                    position = size
                    size - old
                } else {
                    n
                }
            }
        }

        override fun available(): Int {
            synchronized(lock) {
                if (position >= size) return 0
                var total = 0L
                var piece = getPiece(position)
                do {
                    val task = readTasks[piece]
                    if (task == null || !task.isDone) break
                    total += pieceSize
                } while (++piece <= lastPiece)
                total -= position % pieceSize
                return if (total > Int.MAX_VALUE) Int.MAX_VALUE else total.toInt()
            }
        }
    }

    private inner class ReadTask(val piece: Int): Callable<Unit> {

        override fun call() {
            val position = piece * pieceSize.toLong()
            var total = 0
            val pieceLength = if (piece == lastPiece)
                (size - pieceSize.toLong() * lastPiece).toInt() else
                pieceSize
            resource.getInputStream().use { stream ->
                stream.skip(position)
                val temp = ByteArray(pieceSize)
                do {
                    total += stream.read(temp, total, pieceLength - total)
                } while (total != pieceLength)
                synchronized(cacheLock) {
                    cache.seek(position)
                    cache.write(temp, 0, total)
                }
            }
        }
    }
}
