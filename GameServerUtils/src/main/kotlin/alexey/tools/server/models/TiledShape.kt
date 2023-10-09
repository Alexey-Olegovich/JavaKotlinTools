package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.misc.EMPTY_VECTOR2_ARRAY
import alexey.tools.server.serializers.PropertiesDeserializer
import com.badlogic.gdx.math.Vector2
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

class TiledShape(val width: Float = 0F) {

    val type = ""
    val ellipse = false
    val height = 0F
    val id = 0
    val name = ""
    val point = false
    val rotation = 0F
    val visible = false
    val x = 0F
    val y = 0F
    val polyline: Array<Vector2> = EMPTY_VECTOR2_ARRAY
    val polygon: Array<Vector2> = EMPTY_VECTOR2_ARRAY

    @JsonDeserialize(using = PropertiesDeserializer::class)
    val properties = ImmutableVariables.DEFAULT



    override fun toString(): String {
        return "Shape(ellipse=$ellipse, height=$height, id=$id, name='$name', point=$point, rotation=$rotation, visible=$visible, width=$width, x=$x, y=$y, polyline=$polyline, polygon=$polygon, properties=$properties)"
    }
}