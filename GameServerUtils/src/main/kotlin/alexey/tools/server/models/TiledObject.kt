package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.serializers.PropertiesDeserializer
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy::class)
class TiledObject {

    val type = ""
    val gId = 1
    val height = 0F
    val id = 0
    val name = ""
    val rotation = 0F
    val visible = true
    val width = 0F
    val x = 0F
    val y = 0F

    @JsonDeserialize(using = PropertiesDeserializer::class)
    val properties = ImmutableVariables.DEFAULT
}