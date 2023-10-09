package alexey.tools.server.serializers

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.models.DefaultEntity
import alexey.tools.server.models.EntityModel
import alexey.tools.server.models.ShapeModel
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class EntityModelSerializer(private val shapeModelSerializer: Serializer<*>,
                            private val variablesSerializer: Serializer<*>): Serializer<EntityModel>() {

    override fun write(kryo: Kryo, output: Output, entity: EntityModel) {
        val shapes = entity.shapes
        output.writeVarInt(shapes.size, true)
        entity.shapes.forEach { kryo.writeObject(output, it, shapeModelSerializer) }

        kryo.writeObject(output, entity.properties, variablesSerializer)

        output.writeFloat(entity.x)
        output.writeFloat(entity.y)
        output.writeFloat(entity.width)
        output.writeFloat(entity.height)
        output.writeFloat(entity.angle)
        output.writeVarInt(entity.layer, true)
        output.writeString(entity.type)
        output.writeVarLong(entity.id, true)
    }

    override fun read(kryo: Kryo, input: Input, componentClass: Class<out EntityModel>): EntityModel {
        val shapes = ObjectList<ShapeModel>(input.readVarInt(true))
        while (shapes.hasSpace()) shapes.unsafeAdd(kryo.readObject(input, ShapeModel::class.java, shapeModelSerializer))

        val properties = kryo.readObject(input, ImmutableVariables::class.java, variablesSerializer)

        return DefaultEntity(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat(),
            input.readFloat(), input.readVarInt(true), input.readString(), input.readVarLong(true), shapes, properties)
    }
}