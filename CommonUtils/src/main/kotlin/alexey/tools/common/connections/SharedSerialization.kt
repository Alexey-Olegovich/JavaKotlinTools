package alexey.tools.common.connections

import alexey.tools.common.converters.ByteBufferIO
import java.nio.ByteBuffer

class SharedSerialization: Serialization<DataRequest, Any> {

    private val reader = ByteBufferIO()



    override fun write(connection: Connection<DataRequest, Any>, output: ByteBuffer, source: Any) {
        when (source) {
            is DataResponse -> {
                output.put(DataRequest.OPEN)
                ByteBufferIO.writeInt(output, source.size, true)
                if (source.size > 0) output.put(source.data, 0, source.size)
                ByteBufferIO.writeInt(output, source.id, true)
            }
            is TypeResponse -> {
                output.put(DataRequest.TYPE)
                ByteBufferIO.writeUTF8(output, source.type)
                ByteBufferIO.writeInt(output, source.id, true)
            }
            is LengthResponse -> {
                output.put(DataRequest.SIZE)
                ByteBufferIO.writeLong(output, source.length, true)
                ByteBufferIO.writeInt(output, source.id, true)
            }
            is ListResponse -> {
                output.put(DataRequest.LIST)
                ByteBufferIO.writeInt(output, source.list.size, true)
                if (source.list.isNotEmpty())
                    source.list.forEach { ByteBufferIO.writeUTF8(output, it) }
                ByteBufferIO.writeInt(output, source.id, true)
            }
        }
    }

    override fun read(connection: Connection<DataRequest, Any>, input: ByteBuffer): DataRequest {
        reader.byteBuffer = input
        return DataRequest(reader.readString(), ByteBufferIO.readInt(input, true), input.get())
    }
}