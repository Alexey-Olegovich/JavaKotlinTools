package alexey.tools.server.core

import java.io.Closeable

interface Processor : Runnable, Closeable {
    fun enable() {}
    fun disable() {}

    override fun run() {}
    override fun close() {}

    companion object {
        val NULL = object : Processor {}
    }
}