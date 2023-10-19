package alexey.tools.server.models

import alexey.tools.common.collections.convert
import alexey.tools.common.context.ImmutableVariables
import com.badlogic.gdx.math.Vector2

class EntityModel(val x: Float = 0F,
                  val y: Float = 0F,
                  val width: Float = 0F,
                  val height: Float = 0F,
                  val angle: Float = 0F,
                  val layer: Int = 0,
                  val type: String = "",
                  val id: Long = -1,
                  val shapes: List<ShapeModel> = emptyList(),
                  val properties: ImmutableVariables = ImmutableVariables.DEFAULT) {

    val position: Vector2 get() = Vector2(x, y)

    fun with(x: Float = this.x, y: Float = this.y,
             width: Float = this.width, height: Float = this.height,
             rotation: Float = this.angle, layer: Int = this.layer, type: String = this.type,
             properties: ImmutableVariables = ImmutableVariables.DEFAULT): EntityModel =

        EntityModel(x, y, width, height, rotation, layer, type.ifEmpty { this.type }, id,
            scaledShapes(width, height), this.properties + properties)

    fun with(x: Float, y: Float, layer: Int): EntityModel =
        EntityModel(x, y, width, height, angle, layer, type, id, shapes, properties)

    fun with(entityModel: EntityModel): EntityModel =
        EntityModel(entityModel.x, entityModel.y, width, height, entityModel.angle,
            entityModel.layer, type, id, shapes, properties)



    private fun scaledShapes(width: Float, height: Float): List<ShapeModel> {
        if (width == this.width) {
            if (height == this.height) return shapes
            val m = height / this.height
            return shapes.convert { it.scaleHeight(m) }
        }
        if (height == this.height) {
            val m = width / this.width
            return shapes.convert { it.scaleWidth(m) }
        }
        val w = width / this.width
        val h = height / this.height
        return shapes.convert { it.scale(w, h) }
    }



    override fun toString(): String = StringBuilder().apply {
        append("{ x = ")
        append(x)
        append("; y = ")
        append(y)
        append("; width = ")
        append(width)
        append("; height = ")
        append(height)
        append("; angle = ")
        append(angle)
        append("; layer = ")
        append(layer)
        append("; type = ")
        append(type)
        append("; shapes = ")
        append(shapes)
        append("; properties = ")
        append(properties)
        append("; index = ")
        append(id)
        append(" }")
    }.toString()



    companion object {
        val DEFAULT = EntityModel()

        fun newInstance(width: Float = 0F,
                        height: Float = 0F,
                        type: String = "",
                        index: Long = -1,
                        shapeModels: List<ShapeModel> = emptyList(),
                        properties: ImmutableVariables = ImmutableVariables.DEFAULT): EntityModel =
            EntityModel(0F, 0F, width, height, 0F, 0, type, index, shapeModels, properties)
    }
}