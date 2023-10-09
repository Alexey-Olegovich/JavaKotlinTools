package alexey.tools.server.serializers

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.models.EntityModel
import alexey.tools.server.models.ShapeModel
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.SerializerFactory

class EntityModelSerializerFactory: SerializerFactory<EntityModelSerializer> {

    override fun newSerializer(kryo: Kryo, type: Class<*>): EntityModelSerializer =
        EntityModelSerializer(
            kryo.getDefaultSerializer(ShapeModel::class.java),
            kryo.getDefaultSerializer(ImmutableVariables::class.java))

    override fun isSupported(type: Class<*>): Boolean =
        EntityModel::class.java.isAssignableFrom(type)
}