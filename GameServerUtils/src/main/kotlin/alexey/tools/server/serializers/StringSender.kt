package alexey.tools.server.serializers

import alexey.tools.common.collections.IntSet
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.serializers.DefaultSerializers
import com.esotericsoftware.kryo.util.IdentityObjectIntMap
import java.util.function.Function

class StringSender: DefaultSerializers.StringSerializer() {

    private val destinations = IdentityObjectIntMap<Output>()
    private val cache = HashMap<String, Cache>()
    private var nextTextIndex = 0
    private var nextOutputIndex = 0
    private val computeFunction = Function<String, Cache> { Cache(nextTextIndex++) }



    fun remove(output: Output) = destinations.remove(output, -1)



    override fun write(kryo: Kryo, output: Output, text: String) {
        var outputId = destinations.get(output, -1)
        if (outputId == -1) {
            outputId = nextOutputIndex++
            destinations.put(output, outputId)
        }
        val cache = cache.computeIfAbsent(text, computeFunction)
        output.writeVarInt(cache.index, true)
        if (cache.contains.put(outputId)) output.writeString(text)
    }



    private class Cache(val index: Int, val contains: IntSet = IntSet())
}