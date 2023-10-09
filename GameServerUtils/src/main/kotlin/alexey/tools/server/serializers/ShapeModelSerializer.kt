package alexey.tools.server.serializers

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.models.ShapeModel
import alexey.tools.server.misc.EMPTY_VECTOR2_ARRAY
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class ShapeModelSerializer(private val variablesSerializer: Serializer<*>): Serializer<ShapeModel>() {

    override fun write(kryo: Kryo, output: Output, shape: ShapeModel) {
        kryo.writeObject(output, shape.properties, variablesSerializer)

        val vertices = shape.vertices
        output.writeVarInt(vertices.size, true)
        vertices.forEach {
            output.writeFloat(it.x)
            output.writeFloat(it.y)
        }

        output.writeFloat(shape.position.x)
        output.writeFloat(shape.position.y)
        output.writeFloat(shape.width)
        output.writeFloat(shape.height)
        output.writeFloat(shape.rotation)
        output.writeByte(shape.type)
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out ShapeModel>): ShapeModel {
        val properties = kryo.readObject(input, ImmutableVariables::class.java, variablesSerializer)

        val vertices: Array<Vector2>
        val verticesSize = input.readVarInt(true)
        vertices = if (verticesSize == 0) EMPTY_VECTOR2_ARRAY else
            Array(verticesSize) { Vector2(input.readFloat(), input.readFloat()) }

        return ShapeModel(Vector2(input.readFloat(), input.readFloat()),
            input.readFloat(), input.readFloat(), input.readFloat(), input.readByte(), vertices, properties)
    }

}