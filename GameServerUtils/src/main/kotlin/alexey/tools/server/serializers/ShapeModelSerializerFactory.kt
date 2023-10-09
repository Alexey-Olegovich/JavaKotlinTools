package alexey.tools.server.serializers

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.models.ShapeModel
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.SerializerFactory

class ShapeModelSerializerFactory: SerializerFactory<ShapeModelSerializer> {

    override fun newSerializer(kryo: Kryo, type: Class<*>): ShapeModelSerializer =
        ShapeModelSerializer(kryo.getDefaultSerializer(ImmutableVariables::class.java))

    override fun isSupported(type: Class<*>): Boolean =
        ShapeModel::class.java.isAssignableFrom(type)
}