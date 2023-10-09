package alexey.tools.server.models

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy::class)
class TiledGroup {

    val drawOrder = ""
    val id = 0
    val name = ""
    val opacity = 0
    val type = ""
    val visible = true
    val x = 0
    val y = 0
    val objects = emptyList<TiledShape>()



    companion object {
        val NULL = TiledGroup()
    }
}