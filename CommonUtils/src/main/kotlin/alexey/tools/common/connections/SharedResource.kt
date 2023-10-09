package alexey.tools.common.connections

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.convert
import alexey.tools.common.connections.Connection.Listener
import alexey.tools.common.misc.close
import alexey.tools.common.resources.Resource
import java.io.InputStream

class SharedResource(private val shared: Resource,
                     private val bufferSize: Int = 8192): Listener<DataRequest, Any> {

    private val temp = ByteArray(bufferSize)



    override fun onConnect(connection: Connection<DataRequest, Any>) {
        connection.listener = ClientRequests(connection).apply { flush() }
    }



    private inner class ClientRequests(connection: Connection<DataRequest, Any>): QueueListener<DataRequest, Any>(connection) {

        private val streams = ObjectList<InputStream>()



        fun openStream(id: Int, resource: Resource) {
            if (id < 0) return
            val stream = try {
                resource.getInputStream()
            } catch (_: Throwable) {
                sendClosed(id, -1)
                return
            }
            streams.ensureSpace(id)
            streams[id]?.close()
            streams.justSet(id, stream)
            nextBlock(id, stream)
        }

        fun nextBlock(id: Int) {
            if (id < 0) return
            val stream = streams.getOrNull(id) ?: return
            nextBlock(id, stream)
        }

        fun stopStream(id: Int) {
            if (id < 0) return
            val stream = streams.getOrNull(id)
            if (stream != null) {
                stream.close()
                streams.justSetNull(id)
            }
            sendClosed(id, -2)
        }

        fun sendLength(id: Int, resource: Resource) {
            send(LengthResponse(resource.length(), id))
        }

        fun sendType(id: Int, resource: Resource) {
            send(TypeResponse(resource.getContentType(), id))
        }

        fun sendList(id: Int, resource: Resource) {
            send(ListResponse(resource.list().convert { it.getName() }, id))
        }



        override fun onRead(connection: Connection<DataRequest, Any>, message: DataRequest) {
            when (message.type) {
                DataRequest.NEXT -> nextBlock(message.id)
                DataRequest.STOP -> stopStream(message.id)
                else -> {
                    val resource = shared.getResource(message.path)
                    when(message.type) {
                        DataRequest.OPEN -> openStream(message.id, resource)
                        DataRequest.SIZE -> sendLength(message.id, resource)
                        DataRequest.TYPE -> sendType(message.id, resource)
                        DataRequest.LIST -> sendList(message.id, resource)
                    }
                }
            }
        }

        override fun onDisconnect(connection: Connection<DataRequest, Any>) {
            streams.close()
        }

        override fun onError(connection: Connection<DataRequest, Any>, error: Throwable) {
            onDisconnect(connection)
            error.printStackTrace()
        }



        private fun sendClosed(id: Int, status: Int) {
            send(DataResponse(temp, status, id))
        }

        private fun nextBlock(id: Int, stream: InputStream) {
            val buffer = ByteArray(bufferSize)
            val response = try {
                DataResponse(buffer, stream.read(buffer), id)
            } catch (e: Throwable) {
                e.printStackTrace()
                sendClosed(id, -1)
                return
            }
            send(response)
        }
    }
}