package alexey.tools.common.resources

import alexey.tools.common.connections.TCPServer
import alexey.tools.common.connections.SharedResource
import alexey.tools.common.connections.SharedSerialization

open class TCPSharedResource(source: Resource = FileResource(),
                             bufferSize: Int = 1024 * 32,
                             serverBufferSize: Int = bufferSize * 8): ResourceRootWrapper(source) {

    private val server = TCPServer(SharedResource(source, bufferSize), SharedSerialization(),
        serverBufferSize, serverBufferSize, serverBufferSize)



    fun initialize(port: Int) {
        server.initialize(port)
    }

    override fun close() {
        server.shutdown()
    }
}