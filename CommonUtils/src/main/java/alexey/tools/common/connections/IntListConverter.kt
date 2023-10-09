package alexey.tools.common.connections

import alexey.tools.common.collections.IntList
import alexey.tools.common.connections.ObjectSerialization.Decoder
import alexey.tools.common.connections.ObjectSerialization.Encoder
import alexey.tools.common.identity.TypeProperties
import alexey.tools.common.converters.ByteBufferIO
import alexey.tools.common.collections.forEachInt
import java.nio.ByteBuffer

class IntListConverter <T: IntList> (type: Class<T>): Encoder<T>, Decoder<T> {

    private val constructor = type.getDeclaredConstructor(Int::class.java)



    override fun encode(serialization: ObjectSerialization,
                        connection: Connection<*, *>,
                        destination: ByteBuffer,
                        source: T) {

        ByteBufferIO.writeInt(destination, source.size(), true)
        source.forEachInt { ByteBufferIO.writeInt(destination, it, true) }
    }

    override fun decode(serialization: ObjectSerialization,
                        connection: Connection<*, *>,
                        source: ByteBuffer,
                        typeProperties: TypeProperties<T>): T {

        val size = ByteBufferIO.readInt(source, true)
        val result = constructor.newInstance(size)
        var i = 0
        while (i++ < size) result.unsafeAdd(ByteBufferIO.readInt(source, true))
        return result
    }


}