package alexey.tools.server.serializers

import alexey.tools.common.context.ImmutableVariable
import alexey.tools.common.context.ImmutableVariables
import alexey.tools.common.context.Variables
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.SerializerFactory
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class VariablesSerializerFactory: SerializerFactory<VariablesSerializerFactory.DefaultVariablesSerializer> {

    override fun newSerializer(kryo: Kryo, type: Class<*>): DefaultVariablesSerializer =
        if (type === Variables::class.java)
            DefaultVariablesSerializer() else
            CustomVariablesSerializer(type)

    override fun isSupported(type: Class<*>): Boolean =
        Variables::class.java.isAssignableFrom(type)



    class CustomVariablesSerializer(type: Class<*>): DefaultVariablesSerializer() {

        private val constructor = type.getDeclaredConstructor()



        override fun newInstance(): Variables = constructor.newInstance() as Variables
    }

    open class DefaultVariablesSerializer: Serializer<ImmutableVariables>() {

        override fun write(kryo: Kryo, output: Output, variables: ImmutableVariables) {
            val map = variables.asMap()
            output.writeVarInt(map.size, true)
            map.forEach { (k, v) ->
                output.writeString(k)
                output.writeByte(v.type())
                when (v.type()) {
                    ImmutableVariable.BOOLEAN -> output.writeBoolean(v.toBoolean())
                    ImmutableVariable.DECIMAL -> output.writeFloat(v.toFloat())
                    ImmutableVariable.INTEGER -> output.writeVarInt(v.toInt(), true)
                    else -> output.writeString(v.toString())
                }
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out ImmutableVariables>): ImmutableVariables {
            val variables = newInstance()
            var size = input.readVarInt(true)
            while (size-- > 0) {
                val name = input.readString()
                when (input.readByte()) {
                    ImmutableVariable.BOOLEAN -> variables.put(name, input.readBoolean())
                    ImmutableVariable.DECIMAL -> variables.put(name, input.readFloat())
                    ImmutableVariable.INTEGER -> variables.put(name, input.readVarInt(true))
                    else -> variables.put(name, input.readString())
                }
            }
            return variables
        }



        protected open fun newInstance(): Variables = Variables()
    }
}