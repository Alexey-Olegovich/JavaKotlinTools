package alexey.tools.server.serializers

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.ObjectStorage
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.SerializerFactory
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class CollectionSerializerFactory: SerializerFactory<CollectionSerializerFactory.CollectionSerializer> {

    override fun newSerializer(kryo: Kryo, type: Class<*>): CollectionSerializer =
        if (ObjectStorage::class.java.isAssignableFrom(type)) {
            if (type.isAssignableFrom(ObjectList::class.java))
                CollectionSerializer() else
                CustomObjectStorageSerializer(type)
        } else CollectionSerializer()

    override fun isSupported(type: Class<*>): Boolean =
        ObjectStorage::class.java.isAssignableFrom(type)



    class CustomObjectStorageSerializer (type: Class<*>): CollectionSerializer() {

        private val constructor = type.getDeclaredConstructor(Int::class.java)



        override fun read(kryo: Kryo, input: Input, type: Class<out Collection<Any>>): Collection<Any> =
            input.readVarInt(true).let { if (it == 0) newInstance(2) else read(kryo, input, it) }

        @Suppress("unchecked_cast")
        override fun newInstance(capacity: Int) = constructor.newInstance(capacity) as ObjectStorage<Any>
    }

    open class CollectionSerializer: Serializer<Collection<Any>>() {

        override fun write(kryo: Kryo, output: Output, collection: Collection<Any>) {
            val size = collection.size
            output.writeVarInt(collection.size, true)
            if (size == 0) return
            val i = collection.iterator()
            var next = i.next()
            val serializer = kryo.writeClass(output, next.javaClass).serializer
            kryo.writeObject(output, next, serializer)
            while (i.hasNext()) {
                next = i.next()
                kryo.writeObject(output, next, serializer)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Collection<Any>>): Collection<Any> =
            when(val size = input.readVarInt(true)) {
                0 -> emptyList()
                1 -> listOf(kryo.readClassAndObject(input))
                else -> read(kryo, input, size)
            }



        protected fun read(kryo: Kryo, input: Input, size: Int): ObjectStorage<Any> {
            val destination = newInstance(size)
            val registration = kryo.readClass(input)
            val serializer = registration.serializer
            val elementType = registration.type
            while (destination.hasSpace()) destination.unsafeAdd(kryo.readObject(input, elementType, serializer))
            return destination
        }

        protected open fun newInstance(capacity: Int): ObjectStorage<Any> = ObjectList(capacity)
    }
}