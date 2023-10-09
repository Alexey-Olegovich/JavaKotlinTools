package alexey.tools.server.connections

import alexey.tools.common.connections.Serialization
import alexey.tools.server.loaders.BinaryIO
import alexey.tools.server.serializers.StringSender

class ServerBinarySerialization private constructor(binaryIO: BinaryIO,
                                                    private val stringSender: StringSender):
    BinarySerialization(binaryIO) {

    constructor(binaryIO: BinaryIO): this(binaryIO, StringSender().also { binaryIO.register(String::class.java, it) })



    override fun copy(): Serialization<Any, Any> = ServerBinarySerialization(binaryIO, stringSender)

    override fun dispose() {
        stringSender.remove(outputBuffer)
    }
}