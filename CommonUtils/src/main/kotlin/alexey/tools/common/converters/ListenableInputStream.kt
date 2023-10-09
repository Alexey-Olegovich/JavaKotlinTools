package alexey.tools.common.converters

import java.io.InputStream

class ListenableInputStream(private val listener: Listener, private val input: InputStream): InputStream() {

    override fun available() = input.available()
    override fun close() = input.close()
    override fun markSupported() = input.markSupported()
    override fun mark(readlimit: Int) = input.mark(readlimit)
    override fun reset() = input.reset()

    override fun skip(n: Long): Long {
        val skip = input.skip(n)
        if (skip > 0L) listener.onSkip(this, skip)
        return skip
    }

    override fun read(b: ByteArray): Int {
        val read = input.read(b)
        if (read > 0) listener.onRead(this, read)
        return read
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = input.read(b, off, len)
        if (read > 0) listener.onRead(this, read)
        return read
    }

    override fun read(): Int {
        val read = input.read()
        if (read != -1) listener.onRead(this, 1)
        return read
    }



    interface Listener {
        fun onSkip(stream: InputStream, amount: Long) { onRead(stream, amount.toInt()) }
        fun onRead(stream: InputStream, read: Int) {}
    }
}