package alexey.tools.server.serializers

import alexey.tools.common.context.ImmutableVariable
import alexey.tools.common.context.Variables
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class VariablesSerializer: JsonSerializer<Variables>() {

    override fun serialize(value: Variables, gen: JsonGenerator, serializers: SerializerProvider) {
        val map = value.asMap()
        gen.writeStartObject()
        map.forEach { (k, v) ->
            when(v.type()) {
                ImmutableVariable.BOOLEAN -> gen.writeBooleanField(k, v.toBoolean())
                ImmutableVariable.DECIMAL -> gen.writeNumberField (k, v.toFloat()  )
                ImmutableVariable.INTEGER -> gen.writeNumberField (k, v.toInt()    )
                else                      -> gen.writeStringField (k, v.toString() )
            }
        }
        gen.writeEndObject()
    }
}