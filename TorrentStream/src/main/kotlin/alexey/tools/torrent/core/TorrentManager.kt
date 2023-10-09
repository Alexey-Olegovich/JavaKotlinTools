package alexey.tools.torrent.core

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.concurrent.SyncVariable
import alexey.tools.common.misc.PathUtils
import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.ResourceBase
import alexey.tools.common.resources.ResourceRoot
import alexey.tools.common.resources.readBytes
import com.frostwire.jlibtorrent.*
import com.frostwire.jlibtorrent.alerts.Alert
import com.frostwire.jlibtorrent.alerts.AlertType
import com.frostwire.jlibtorrent.alerts.PieceFinishedAlert
import com.frostwire.jlibtorrent.alerts.TorrentDeletedAlert
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.NullPointerException
import java.nio.file.Files
import java.nio.file.Paths

class TorrentManager(val sessionManager: SessionManager,
                     val saveDirectory: File): Closeable {

    constructor(saveDirectory: String = "."):
            this(File(saveDirectory))

    constructor(saveDirectory: File = File(".")):
            this(SessionManager(), saveDirectory)

    private val holders = HashMap<Sha1Hash, Holder>()



    init {
        sessionManager.addListener(MainListener())
        sessionManager.start()
        sessionManager.startDht()
    }



    fun obtainTorrent(source: Resource) =
        TorrentResource(source)

    fun getTorrent(source: Resource): Resource =
        FreeTorrentResource(source)



    override fun close() {
        sessionManager.stop()
        synchronized(holders) { holders.values.forEach { it.torrent.get()?.close0() } }
    }



    inner class FreeTorrentResource(source: Resource): TorrentResourceBase(source) {
        override val holder = synchronized(holders) { holders[hash] } ?: throw NullPointerException()
    }

    inner class TorrentResource(source: Resource): TorrentResourceBase(source) {

        override val holder = obtainHolder()



        private fun obtainHolder(): Holder {
            synchronized(holders) {
                var e = holders[hash]
                if (e == null) {
                    e = Holder(SyncVariable { createTorrent() })
                    holders[hash] = e
                } else e.count++
                return e
            }
        }

        private fun createTorrent(): TorrentData {
            sessionManager.download(info, File(saveDirectory, hash.toHex()))
            return TorrentData(sessionManager.find(hash))
        }
    }

    abstract inner class TorrentResourceBase(val source: Resource): ResourceBase(), ResourceRoot {

        protected val info = TorrentInfo(source.readBytes())
        protected val files: FileStorage = info.files()
        protected val hash: Sha1Hash = info.infoHash()
        protected abstract val holder: Holder



        fun getResource(index: Int) = TorrentResourceEntry(index)

        fun getFilePath(index: Int): String = files.filePath(index).replace('\\', '/')

        fun getFileName(index: Int): String = files.fileName(index)



        override fun getResource(relativePath: String): Resource {
            if (relativePath.isEmpty()) return this
            for (i in 0 ..< files.numFiles()) if (getFilePath(i) == relativePath)
                return TorrentResourceEntry(i)
            return Resource.NULL
        }

        override fun list(): List<TorrentResourceEntry> {
            val totalFiles = files.numFiles()
            val result = ObjectList<TorrentResourceEntry>(totalFiles)
            for (i in 0 ..< totalFiles) result.unsafeAdd(TorrentResourceEntry(i))
            return result
        }

        override fun getName(): String = info.name()

        override fun getInputStream(): InputStream = source.getInputStream()

        override fun getOutputStream(): OutputStream = source.getOutputStream()

        override fun getContentType(): String = "application/x-bittorrent"

        override fun canRead(): Boolean = source.canRead()

        override fun canWrite(): Boolean = source.canWrite()

        override fun getPath(): String = source.toString()

        override fun isValid(): Boolean = source.isValid()

        override fun getResourceType(): String = "torrent"

        override fun length(): Long = info.totalSize()

        override fun close() {
            synchronized(holders) {
                val holder = holders[hash] ?: return
                if (--holder.count != 0) return
                holders.remove(hash)
                val torrent = holder.torrent.remove() ?: return
                torrent.close()
                sessionManager.remove(torrent.handle, SessionHandle.DELETE_FILES)
            }
        }



        inner class TorrentResourceEntry(val index: Int): ResourceBase() {

            private val entry by lazy { holder.torrent.obtain().obtain(index) }



            fun getFileName() = getFileName(index)



            override fun getName() = getFilePath(index)

            override fun getInputStream(): InputStream = entry.getInputStream()

            override fun getPath(): String = source.toString() + "!/" + getName()

            override fun length(): Long = files.fileSize(index)

            override fun canRead(): Boolean = true

            override fun canWrite(): Boolean = false

            override fun isValid(): Boolean = true

            override fun getResourceType(): String = "torrent-entry"

            override fun getContentType(): String = Files.probeContentType(Paths.get(getFileName())) ?: super.getContentType()

            override fun getResource(relativePath: String): Resource {
                if (relativePath.isEmpty()) return this
                val normalizedPath = PathUtils.normalizePath(getName(), relativePath)
                for (i in 0 ..< files.numFiles()) if (getFilePath(i) == normalizedPath)
                    return TorrentResourceEntry(i)
                return Resource.NULL
            }
        }
    }



    private inner class MainListener: AlertListener {
        override fun types(): IntArray = intArrayOf(
            AlertType.PIECE_FINISHED.swig(),
            AlertType.TORRENT_DELETED.swig())

        override fun alert(alert: Alert<*>) {
            if (alert.type() == AlertType.TORRENT_DELETED) {
                alert as TorrentDeletedAlert
                File(saveDirectory, alert.infoHash.toHex()).delete()
            } else {
                alert as PieceFinishedAlert
                val hash = alert.handle().infoHash()
                val torrent = (synchronized(holders) { holders[hash] } ?: return)
                    .torrent.get() ?: return
                torrent.pieceFinished(alert.pieceIndex())
                torrent.doRequests()
            }
        }
    }

    class Holder(val torrent: SyncVariable<TorrentData>, var count: Int = 1)
}