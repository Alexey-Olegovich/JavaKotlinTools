package alexey.tools.server.serializers

import alexey.tools.common.collections.ObjectStorage
import alexey.tools.common.collections.getOrExtendSet
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.serializers.DefaultSerializers

class StringReceiver: DefaultSerializers.StringSerializer() {

    private val cache = ObjectStorage<String>()



    fun clear() {
        cache.clear()
    }



    override fun read(kryo: Kryo, input: Input, type: Class<out String>): String =
        cache.getOrExtendSet(input.readVarInt(true)) { input.readString() }
}