package alexey.tools.server.serializers

import alexey.tools.common.context.Variables
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class VariablesDeserializer: JsonDeserializer<Variables>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Variables {
        val variables = Variables()
        var nextToken = p.nextToken()
        while (nextToken != JsonToken.END_OBJECT) {
            when(p.nextToken()) {
                JsonToken.VALUE_NUMBER_INT   -> variables.put(p.currentName, p.intValue)
                JsonToken.VALUE_NUMBER_FLOAT -> variables.put(p.currentName, p.floatValue)
                JsonToken.VALUE_TRUE         -> variables.put(p.currentName, true)
                JsonToken.VALUE_FALSE        -> variables.put(p.currentName, false)
                else                         -> variables.put(p.currentName, p.getValueAsString(""))
            }
            nextToken = p.nextToken()
        }
        return variables
    }
}