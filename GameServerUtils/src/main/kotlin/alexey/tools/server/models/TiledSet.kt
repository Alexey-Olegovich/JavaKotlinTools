package alexey.tools.server.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy::class)
class TiledSet(val tileCount: Int = 0) {

    val columns = 0
    val image = ""
    val imageHeight = 0
    val imageWidth = 0
    val margin = 0
    val name = ""
    val spacing = 0
    val tiledVersion = ""
    val tileHeight = 0
    val tiles = emptyList<TiledUnit>()
    val tileWidth = 0
    val version = ""
    val type = ""

    @JsonProperty("class")
    val contentClass = ""



    companion object {
        val EMPTY = TiledSet()
    }
}