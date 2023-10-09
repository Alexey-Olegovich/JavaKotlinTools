package alexey.tools.common.resources

import alexey.tools.common.connections.Connection
import alexey.tools.common.connections.DataRequest
import alexey.tools.common.connections.TCPClient
import alexey.tools.common.misc.halve
import alexey.tools.common.misc.safeApply
import alexey.tools.common.connections.RemoteResource
import alexey.tools.common.connections.RemoteSerialization

class TCPRemoteResource(private val bufferSize: Int = 1024 * 32,
                        private val streamSize: Int = bufferSize * 2,
                        serverBufferSize: Int = bufferSize * 8): ResourceBase(), ResourceRoot {

    private val remote = RemoteResource()
    private val client = TCPClient(remote, RemoteSerialization(bufferSize), serverBufferSize)



    fun getClient(): Connection<Any, DataRequest> = remote

    fun initialize(address: String, port: Int) {
        client.initialize(address, port)
    }



    override fun getResource(relativePath: String): Resource = remote.getResource(relativePath, bufferSize, streamSize)
    override fun getResourceType(): String = "remote"
    override fun getPath(): String = client.address
    override fun list(): List<Resource> = getResource("").list()

    override fun close() {
        client.shutdown()
    }



    companion object {
        fun newInstance(path: String): TCPRemoteResource {
            val pair = path.halve('/')
            val port = pair.second.toInt()
            return TCPRemoteResource().safeApply { initialize(pair.first, port) }
        }
    }
}