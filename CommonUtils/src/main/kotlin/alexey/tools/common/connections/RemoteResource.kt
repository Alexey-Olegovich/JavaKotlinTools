package alexey.tools.common.connections

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.convert
import alexey.tools.common.concurrent.LazyVariable
import alexey.tools.common.identity.IdFactory
import alexey.tools.common.converters.AppendableInputStream
import alexey.tools.common.converters.LazyInputStream
import alexey.tools.common.misc.PathUtils.*
import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.ResourceBase
import java.io.Closeable
import java.io.InputStream

class RemoteResource: SyncQueueListener<Any, DataRequest>() {

    private var entries: ObjectList<RemoteResourceEntry>? = ObjectList()
    private val entryIdFactory = IdFactory()

    private var streams: ObjectList<RemoteResourceEntry.RemoteStream>? = ObjectList()
    private val streamIdFactory = IdFactory()



    fun getResource(relativePath: String, bufferSize: Int, streamSize: Int = bufferSize * 4): Resource =
        RemoteResourceEntry(relativePath, bufferSize, streamSize)



    override fun onRead(connection: Connection<Any, DataRequest>, message: Any) {
        when (message) {
            is DataResponse -> if (message.size < -1)
                removeStream(message.id) else
                getStream(message.id)?.put(message.data, message.size)
            is TypeResponse -> removeEntry(message.id)?.setContentType(message.type)
            is ListResponse -> removeEntry(message.id)?.setList(message.list)
            is LengthResponse -> removeEntry(message.id)?.setLength(message.length)
        }
        super.onRead(connection, message)
    }

    override fun onDisconnect(connection: Connection<Any, DataRequest>) {
        stop()
        super.onDisconnect(connection)
    }

    override fun onError(connection: Connection<Any, DataRequest>, error: Throwable) {
        stop()
        super.onError(connection, error)
    }

    override fun send(message: DataRequest) {
        throw UnsupportedOperationException("send")
    }



    private fun stop() {
        synchronized(streamIdFactory) {
            val streams = streams ?: return
            while (streams.isNotEmpty) streams.removeLast()?.justClose()
            streamIdFactory.clear()
            this.streams = null
        }
        synchronized(entryIdFactory) {
            val entries = entries ?: return
            while (entries.isNotEmpty) entries.removeLast()?.close()
            entryIdFactory.clear()
            this.entries = null
        }
    }

    private fun send(path: String, id: Int, type: Byte) {
        super.send(DataRequest(path, id, type))
    }

    private fun removeEntry(id: Int): RemoteResourceEntry? {
        synchronized(entryIdFactory) {
            val entries = entries ?: return null
            if (entries.outBounds(id)) return null
            val entry = entries[id] ?: return null
            entries.justSetNull(id)
            entryIdFactory.unsafeFree(id)
            return entry
        }
    }

    private fun getStream(id: Int): AppendableInputStream? {
        synchronized(streamIdFactory) {
            val streams = streams ?: return null
            if (streams.outBounds(id)) return null
            return streams[id]
        }
    }

    private fun removeStream(id: Int): AppendableInputStream? {
        synchronized(streamIdFactory) {
            val streams = streams ?: return null
            if (streams.outBounds(id)) return null
            val stream = streams[id] ?: return null
            streams.justSetNull(id)
            streamIdFactory.unsafeFree(id)
            return stream
        }
    }



    private inner class RemoteResourceEntry(private val path: String,
                                            private val bufferSize: Int,
                                            private val streamSize: Int = bufferSize * 4): ResourceBase(), Closeable {

        private val type = LazyVariable<String> { requestInfo(DataRequest.TYPE) }
        private var length = LazyVariable<Long> { requestInfo(DataRequest.SIZE) }
        private var list = LazyVariable<List<Resource>> { requestInfo(DataRequest.LIST) }



        override fun getResource(relativePath: String): Resource =
            RemoteResourceEntry(concatenatePaths(path, relativePath), bufferSize, streamSize)

        override fun getContentType() = type.obtain()

        override fun length() = length.obtain()

        override fun getInputStream(): InputStream {
            val stream = RemoteStream()
            val id: Int
            synchronized(streamIdFactory) {
                val streams = streams ?: throw IllegalStateException("closed")
                id = streamIdFactory.obtain()
                streams.extendSet(id, stream)
            }
            stream.id = id
            send(path, id, DataRequest.OPEN)
            return stream
        }

        override fun getPath() = joinPaths(address, path)

        override fun getResourceType(): String = "remote-entry"

        override fun list(): List<Resource> = list.obtain()

        override fun canRead(): Boolean = length() > 0L

        override fun close() {
            setContentType("application/octet-stream")
            setLength(-1L)
            setList(emptyList())
        }



        fun setContentType(contentType: String) =
            type.set(contentType)

        fun setLength(len: Long) =
            length.set(len)

        fun setList(names: List<String>) =
            list.set(names.convert { RemoteResourceEntry(joinPaths(path, it), bufferSize, streamSize) })



        private fun nextBlock(id: Int) = send("", id, DataRequest.NEXT)

        private fun stopStream(id: Int) = send("", id, DataRequest.STOP)

        private fun requestInfo(type: Byte) {
            if (type < DataRequest.SIZE) throw IllegalStateException()
            val id: Int
            synchronized(entryIdFactory) {
                val entries = entries ?: throw IllegalStateException("closed")
                id = entryIdFactory.obtain()
                entries.extendSet(id, this)
            }
            send(path, id, type)
        }



        inner class RemoteStream(var id: Int = -1): LazyInputStream(streamSize) {

            private var requested = true
            private var closed = false



            fun justClose() {
                synchronized(buffer) {
                    if (closed) return
                    closed = true
                    super.close0()
                }
            }



            override fun onRead() {
                if (requested || buffer.size - limit + position < bufferSize) return
                nextBlock(id)
                requested = true
            }

            override fun onPut() {
                if (buffer.size - limit + position < bufferSize) {
                    requested = false
                } else {
                    nextBlock(id)
                }
            }

            override fun close0() {
                if (closed) return
                closed = true
                super.close0()
                stopStream(id)
            }
        }
    }
}