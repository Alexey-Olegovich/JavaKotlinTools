package alexey.tools.server.serializers

import alexey.tools.common.loaders.PathGroup
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class ResourceGroupDeserializer<T>(private val group: PathGroup<T>): JsonDeserializer<T>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T = group.obtainObject(p.text)
}