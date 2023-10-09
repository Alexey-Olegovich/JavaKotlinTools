package alexey.tools.server.models

import alexey.tools.common.collections.ImmutableIntList
import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.serializers.EntityIdChunkDeserializer
import alexey.tools.server.serializers.PropertiesDeserializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy::class)
class TiledLayer(val width: Int = 0,
                 val height: Int = 0) {

    val chunks = emptyList<TiledChunk>()
    val objects = emptyList<TiledObject>()
    val id = 0
    val name = ""
    val opacity = 0
    val startX = 0
    val startY = 0
    val drawOrder = ""
    val offsetX = 0F
    val offsetY = 0F
    val type = ""
    val visible = true
    val x = 0
    val y = 0
    val compression = ""
    val encoding = ""

    @JsonProperty("class")
    val contentClass = ""

    @JsonDeserialize(using = EntityIdChunkDeserializer::class)
    val data: ImmutableIntList = ImmutableIntList.EMPTY

    @JsonDeserialize(using = PropertiesDeserializer::class)
    val properties = ImmutableVariables.DEFAULT
}