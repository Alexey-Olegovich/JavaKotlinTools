package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables

class DefaultEntity(override val x: Float = 0F,
                    override val y: Float = 0F,
                    override val width: Float = 0F,
                    override val height: Float = 0F,
                    override val angle: Float = 0F,
                    override val layer: Int = 0,
                    override val type: String = "",
                    override val id: Long = -1,
                    override val shapes: List<ShapeModel> = emptyList(),
                    override val properties: ImmutableVariables = ImmutableVariables.DEFAULT): EntityModel {

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
}