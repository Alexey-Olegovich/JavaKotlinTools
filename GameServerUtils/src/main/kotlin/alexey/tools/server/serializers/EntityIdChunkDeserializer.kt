package alexey.tools.server.serializers

import alexey.tools.common.collections.ImmutableIntList
import alexey.tools.common.collections.IntList
import alexey.tools.common.converters.decompressZLib
import alexey.tools.common.converters.toIntArray
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class EntityIdChunkDeserializer: JsonDeserializer<ImmutableIntList>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ImmutableIntList =
        IntList.wrap(p.binaryValue.decompressZLib().toIntArray())
}