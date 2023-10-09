package alexey.tools.server.serializers

import alexey.tools.common.collections.CachedIntSet
import alexey.tools.common.collections.ImmutableIntSet
import alexey.tools.common.collections.IntList
import alexey.tools.common.collections.IntListSingleton
import alexey.tools.common.collections.IntSet
import alexey.tools.common.collections.IntSetSingleton
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.SerializerFactory
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class IntSetSerializerFactory: SerializerFactory<Serializer<ImmutableIntSet>> {

    override fun newSerializer(kryo: Kryo, type: Class<*>): Serializer<ImmutableIntSet> =
        when {
            type === IntSet::class.java -> DefaultIntSetSerializer()
            type === CachedIntSet::class.java -> CachedIntSetSerializer()
            type === IntListSingleton::class.java -> SingletonIntSetSerializer()
            type === IntList.EMPTY.javaClass -> EmptyIntSetSerializer()
            else -> CustomIntSetSerializer(type)
        }

    override fun isSupported(type: Class<*>): Boolean =
        type === IntSetSingleton::class.java ||
        type === IntSet.EMPTY.javaClass ||
        IntSet::class.java.isAssignableFrom(type)



    class CustomIntSetSerializer (type: Class<*>): DefaultIntSetSerializer() {

        private val constructor = type.getDeclaredConstructor(Int::class.java)



        override fun newInstance(capacity: Int) = constructor.newInstance(capacity) as IntSet
    }

    class EmptyIntSetSerializer: Serializer<ImmutableIntSet>() {
        override fun write(kryo: Kryo, output: Output, list: ImmutableIntSet) {}

        override fun read(kryo: Kryo, input: Input, type: Class<out ImmutableIntSet>): ImmutableIntSet {
            return IntSet.EMPTY
        }
    }

    class SingletonIntSetSerializer: Serializer<ImmutableIntSet>() {
        override fun write(kryo: Kryo, output: Output, list: ImmutableIntSet) {
            output.writeVarInt(list.first(), true)
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out ImmutableIntSet>): ImmutableIntSet {
            return IntSetSingleton(input.readVarInt(true))
        }
    }

    open class CachedIntSetSerializer: DefaultIntSetSerializer() {
        override fun newInstance(capacity: Int) = CachedIntSet(capacity)
    }

    open class DefaultIntSetSerializer: Serializer<ImmutableIntSet>() {

        override fun write(kryo: Kryo, output: Output, set: ImmutableIntSet) {
            var i = set.capacity()
            while (--i >= 0) if (set.getWord(i) != 0L) break
            output.writeVarInt(i + 1, true)
            while (i >= 0) output.writeVarLong(set.getWord(i--), true)
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out ImmutableIntSet>): ImmutableIntSet {
            var i = input.readVarInt(true)
            val set = newInstance(i)
            while (--i >= 0) set.setWord(i, input.readVarLong(true))
            return set
        }

        protected open fun newInstance(capacity: Int) = IntSet(capacity)
    }
}