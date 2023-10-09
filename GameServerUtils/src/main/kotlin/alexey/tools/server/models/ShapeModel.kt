package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.misc.EMPTY_VECTOR2_ARRAY
import com.badlogic.gdx.math.Vector2

class ShapeModel(val position: Vector2 = Vector2.Zero,
                 val width: Float = 0F,
                 val height: Float = 0F,
                 val rotation: Float = 0F,

                 val type: Byte = BOX,
                 val vertices: Array<Vector2> = EMPTY_VECTOR2_ARRAY,

                 val properties: ImmutableVariables = ImmutableVariables.DEFAULT) {

    fun scale(w: Float, h: Float): ShapeModel = ShapeModel(Vector2(position.x * w, position.y * h), width * w, height * h,
        rotation, type, Array(vertices.size) { vertices[it].let { v -> Vector2(v).scl(w, h) } }, properties)

    fun scaleWidth(w: Float): ShapeModel = ShapeModel(Vector2(position.x * w, position.y), width * w, height,
        rotation, type, Array(vertices.size) { vertices[it].let { v -> Vector2(v.x * w, v.y) } }, properties)

    fun scaleHeight(h: Float): ShapeModel = ShapeModel(Vector2(position.x, position.y * h), width, height * h,
        rotation, type, Array(vertices.size) { vertices[it].let { v -> Vector2(v.x, v.y * h) } }, properties)

    override fun toString(): String = StringBuilder().apply {
        append("{x=")
        append(position.x)
        append(";y=")
        append(position.y)
        append(";width=")
        append(width)
        append(";height=")
        append(height)
        append(";rotation=")
        append(rotation)
        append(";type=")
        append(type)
        append(";vertices=")
        append(vertices.contentToString())
        append(";properties=")
        append(properties)
        append("}")
    }.toString()

    companion object {
        const val BOX: Byte = 0
        const val POINT: Byte = 1
        const val CIRCLE: Byte = 2
        const val POLYGON: Byte = 3
        const val POLYLINE: Byte = 4

        fun newPolygon(vertices: Array<Vector2>, properties: ImmutableVariables = ImmutableVariables.DEFAULT) =
            ShapeModel(type = POLYGON, vertices = vertices, properties = properties)
        fun newPolyline(vertices: Array<Vector2>, properties: ImmutableVariables = ImmutableVariables.DEFAULT) =
            ShapeModel(type = POLYLINE, vertices = vertices, properties = properties)
        fun newBox(x: Float, y: Float, width: Float, height: Float, properties: ImmutableVariables = ImmutableVariables.DEFAULT) =
            ShapeModel(Vector2(x, y), width, height, properties = properties)
        fun newCircle(x: Float, y: Float, radius: Float, properties: ImmutableVariables = ImmutableVariables.DEFAULT) =
            ShapeModel(Vector2(x, y), radius, type = CIRCLE, properties = properties)
    }
}