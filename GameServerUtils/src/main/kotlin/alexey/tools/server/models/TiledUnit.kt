package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.serializers.PropertiesDeserializer
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy::class)
class TiledUnit(val id: Int = 0) {

    val type = ""
    val objectGroup = TiledGroup.NULL
    val probability = 0F

    @JsonDeserialize(using = PropertiesDeserializer::class)
    val properties = ImmutableVariables.DEFAULT
}