package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.common.collections.convert

interface EntityModel {
    val x: Float
    val y: Float
    val width: Float
    val height: Float
    val angle: Float
    val layer: Int
    val type: String
    val id: Long
    val shapes: List<ShapeModel>
    val properties: ImmutableVariables



    fun with(x: Float = this.x, y: Float = this.y,
             width: Float = this.width, height: Float = this.height,
             rotation: Float = this.angle, layer: Int = this.layer, type: String = this.type,
             properties: ImmutableVariables = ImmutableVariables.DEFAULT): DefaultEntity =

        DefaultEntity(x, y, width, height, rotation, layer, type.ifEmpty { this.type }, id,
            scaledShapes(width, height), this.properties + properties)

    fun with(x: Float, y: Float, layer: Int): DefaultEntity =
        DefaultEntity(x, y, width, height, angle, layer, type, id, shapes, properties)

    fun with(entityModel: EntityModel): DefaultEntity =
        DefaultEntity(entityModel.x, entityModel.y, width, height, entityModel.angle,
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



    companion object {
        val DEFAULT: EntityModel = DefaultEntity()

        fun newInstance(width: Float = 0F,
                        height: Float = 0F,
                        type: String = "",
                        index: Long = -1,
                        shapeModels: List<ShapeModel> = emptyList(),
                        properties: ImmutableVariables = ImmutableVariables.DEFAULT): DefaultEntity =
            DefaultEntity(0F, 0F, width, height, 0F, 0, type, index, shapeModels, properties)
    }
}