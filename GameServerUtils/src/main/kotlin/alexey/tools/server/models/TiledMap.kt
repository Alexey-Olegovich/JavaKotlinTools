package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.serializers.PropertiesDeserializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy::class)
class TiledMap {

    val backgroundColor = ""
    val compressionLevel = -1
    val height = 0
    val infinite = true
    val layers = emptyList<TiledLayer>()
    val nextLayerId = 0
    val nextObjectId = 0
    val orientation = ""
    val renderOrder = ""
    val tiledVersion = ""
    val tileHeight = 0
    val tileSets = emptyList<TiledReference>()
    val tileWidth = 0
    val version = ""
    val width = 0
    val type = ""

    @JsonProperty("class")
    val contentClass = ""

    @JsonDeserialize(using = PropertiesDeserializer::class)
    val properties = ImmutableVariables.DEFAULT



    @JsonNaming(PropertyNamingStrategies.LowerCaseStrategy::class)
    class TiledReference {

        val firstGId = 0
        val source = ""
    }
}