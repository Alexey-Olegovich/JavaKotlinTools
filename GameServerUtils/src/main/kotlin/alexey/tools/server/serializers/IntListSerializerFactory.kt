package alexey.tools.server.serializers

import alexey.tools.common.collections.ImmutableIntList
import alexey.tools.common.collections.IntList
import alexey.tools.common.collections.IntListSingleton
import alexey.tools.common.collections.forEachInt
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.SerializerFactory
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class IntListSerializerFactory: SerializerFactory<Serializer<ImmutableIntList>> {

    override fun newSerializer(kryo: Kryo, type: Class<*>): Serializer<ImmutableIntList> =
        when {
            type === IntList::class.java -> DefaultIntListSerializer()
            type === IntListSingleton::class.java -> SingletonIntListSerializer()
            type === IntList.EMPTY.javaClass -> EmptyIntListSerializer()
            else -> CustomIntListSerializer(type)
        }

    override fun isSupported(type: Class<*>): Boolean =
        type === IntListSingleton::class.java ||
        type === IntList.EMPTY.javaClass ||
        IntList::class.java.isAssignableFrom(type)



    class CustomIntListSerializer (type: Class<*>): DefaultIntListSerializer() {

        private val constructor = type.getDeclaredConstructor(Int::class.java)



        override fun newInstance(capacity: Int) = constructor.newInstance(capacity) as IntList
    }

    class EmptyIntListSerializer: Serializer<ImmutableIntList>() {
        override fun write(kryo: Kryo, output: Output, list: ImmutableIntList) {}

        override fun read(kryo: Kryo, input: Input, type: Class<out ImmutableIntList>): ImmutableIntList {
            return IntList.EMPTY
        }
    }

    class SingletonIntListSerializer: Serializer<ImmutableIntList>() {
        override fun write(kryo: Kryo, output: Output, list: ImmutableIntList) {
            output.writeVarInt(list.first(), true)
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out ImmutableIntList>): ImmutableIntList {
            return IntListSingleton(input.readVarInt(true))
        }
    }

    open class DefaultIntListSerializer: Serializer<ImmutableIntList>() {

        override fun write(kryo: Kryo, output: Output, list: ImmutableIntList) {
            output.writeVarInt(list.size(), true)
            list.forEachInt { output.writeVarInt(it, true) }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out ImmutableIntList>): ImmutableIntList {
            val list = newInstance(input.readVarInt(true))
            while (list.hasSpace()) list.unsafeAdd(input.readVarInt(true))
            return list
        }

        protected open fun newInstance(capacity: Int) = IntList(capacity)
    }
}