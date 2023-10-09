package alexey.tools.server.connections

import alexey.tools.common.connections.Connection
import alexey.tools.common.connections.Serialization
import alexey.tools.server.loaders.BinaryIO
import com.esotericsoftware.kryo.io.ByteBufferInput
import com.esotericsoftware.kryo.io.ByteBufferOutput
import java.nio.ByteBuffer

open class BinarySerialization(val binaryIO: BinaryIO): Serialization<Any, Any> {

    protected val inputBuffer = ByteBufferInput()
    protected val outputBuffer = ByteBufferOutput()



    override fun write(connection: Connection<Any, Any>, output: ByteBuffer, source: Any) {
        outputBuffer.setBuffer(output)
        binaryIO.writeObject(outputBuffer, source)
    }

    override fun read(connection: Connection<Any, Any>, input: ByteBuffer): Any {
        inputBuffer.setBuffer(input)
        return binaryIO.readObject(inputBuffer)
    }

    override fun copy(): Serialization<Any, Any> = BinarySerialization(binaryIO)
}