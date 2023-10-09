package alexey.tools.common.resources

class TCPSharedResourceRoot(private val source: ResourceRoot,
                            bufferSize: Int = 8192,
                            serverBufferSize: Int = bufferSize * 2):
    TCPSharedResource(source, bufferSize, serverBufferSize) {

    override fun close() {
        super.close()
        source.close()
    }
}