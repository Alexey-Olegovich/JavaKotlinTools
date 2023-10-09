package alexey.tools.server.serializers

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.common.context.Variables
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class PropertiesDeserializer: JsonDeserializer<ImmutableVariables>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ImmutableVariables {
        val m = Variables()
        var nt = p.nextToken()
        while (nt != JsonToken.END_ARRAY) {
            p.nextToken()
            p.nextToken()
            val name = p.getValueAsString("")
            p.nextToken()
            p.nextToken()
            p.nextToken()
            when(p.nextToken()) {
                JsonToken.VALUE_NUMBER_INT   -> m.put(name, p.intValue)
                JsonToken.VALUE_NUMBER_FLOAT -> m.put(name, p.floatValue)
                JsonToken.VALUE_TRUE         -> m.put(name, true)
                else                         -> m.put(name, p.getValueAsString(""))
            }
            p.nextToken()
            nt = p.nextToken()
        }
        return m
    }
}