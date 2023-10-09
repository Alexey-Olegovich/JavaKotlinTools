package alexey.tools.common.converters

import alexey.tools.common.misc.MiscUtils
import alexey.tools.common.resources.AdvancedURLResource
import java.io.InputStream

class PartialURLInputStream(private val resource: AdvancedURLResource): InputStream() {

    private val lock = MiscUtils.newObject()
    private var inputStream: InputStream? = null
    private var closed = false



    override fun available() = synchronized(lock) { obtain().available() }

    override fun read(b: ByteArray): Int {
        synchronized(lock) {
            if (closed) return -1
            return obtain().read(b)
        }
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        synchronized(lock) {
            if (closed) return -1
            return obtain().read(b, off, len)
        }
    }

    override fun read(): Int {
        synchronized(lock) {
            if (closed) return -1
            return obtain().read()
        }
    }

    override fun skip(n: Long): Long {
        synchronized(lock) {
            if (closed) return 0L
            var inputStream = inputStream
            if (n < 1L) {
                if (inputStream != null) return 0L
                inputStream = resource.openConnection().getInputStream()
                this.inputStream = inputStream
                return 0L
            }
            if (inputStream != null) return inputStream.skip(n)
            val connection = resource.openConnection(n)
            inputStream = connection.getInputStream()
            this.inputStream = inputStream
            val range = connection.getHeaderField("Content-Range")
            return if (range == null)
                inputStream.skip(n) else
                range.substring(6, range.indexOf('-', 7)).toLong()
        }
    }

    override fun close() {
        synchronized(lock) {
            inputStream?.close()
            closed = true
        }
    }



    private fun obtain(): InputStream {
        var inputStream = inputStream
        if (inputStream != null) return inputStream
        inputStream = resource.openConnection().getInputStream()
        this.inputStream = inputStream
        return inputStream
    }
}