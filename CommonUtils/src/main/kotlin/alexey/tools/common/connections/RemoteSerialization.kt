package alexey.tools.common.connections

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.converters.ByteBufferIO
import java.nio.ByteBuffer

class RemoteSerialization(bufferSize: Int = 8192): Serialization<Any, DataRequest> {

    private val buffer = ByteArray(bufferSize)
    private val reader = ByteBufferIO()



    override fun write(connection: Connection<Any, DataRequest>, output: ByteBuffer, source: DataRequest) {
        ByteBufferIO.writeUTF8(output, source.path)
        ByteBufferIO.writeInt(output, source.id, true)
        output.put(source.type)
    }

    override fun read(connection: Connection<Any, DataRequest>, input: ByteBuffer): Any = when (input.get()) {
        DataRequest.OPEN -> {
            val size = ByteBufferIO.readInt(input, true)
            if (size > 0) input.get(buffer, 0, size)
            DataResponse(buffer, size, ByteBufferIO.readInt(input, true))
        }
        DataRequest.TYPE -> {
            reader.byteBuffer = input
            TypeResponse(reader.readString(), ByteBufferIO.readInt(input, true))
        }
        DataRequest.SIZE -> {
            LengthResponse(ByteBufferIO.readLong(input, true), ByteBufferIO.readInt(input, true))
        }
        DataRequest.LIST -> {
            val size = ByteBufferIO.readInt(input, true)
            if (size == 0) {
                ListResponse(emptyList(), ByteBufferIO.readInt(input, true))
            } else {
                val list = ObjectList<String>(size)
                reader.byteBuffer = input
                while (list.size != size) list.unsafeAdd(reader.readString())
                ListResponse(list, ByteBufferIO.readInt(input, true))
            }
        }
        else -> throw IllegalStateException()
    }

}