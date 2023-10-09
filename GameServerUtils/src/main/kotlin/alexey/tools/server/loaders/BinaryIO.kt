package alexey.tools.server.loaders

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.loaders.ObjectIO
import alexey.tools.common.collections.convert
import alexey.tools.common.misc.Injector
import alexey.tools.common.mods.ModLoader
import alexey.tools.common.resources.Resource
import com.badlogic.gdx.utils.ObjectSet
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.SerializerFactory
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import java.lang.reflect.Modifier

class BinaryIO(val modLoader: ModLoader): ObjectIO, Injector {

    private val kryo = Kryo()
    private val defaultSerializerAdded = ObjectSet<Class<*>>()



    fun writeObject(output: Output, obj: Any) =
        synchronized(kryo) { kryo.writeClassAndObject(output, obj) }

    fun readObject(input: Input): Any =
        synchronized(kryo) { kryo.readClassAndObject(input) ?: throw NullPointerException() }

    fun register(type: Class<*>): Unit =
        synchronized(kryo) { kryo.register(type) }

    fun register(type: Class<*>, serializer: Serializer<*>): Unit =
        synchronized(kryo) { kryo.register(type, serializer) }

    fun register(vararg types: Class<*>) =
        synchronized(kryo) { types.forEach { kryo.register(it) } }

    fun addDefaultSerializer(type: Class<*>, serializerFactory: SerializerFactory<*>): Boolean {
        synchronized(kryo) {
            if (!defaultSerializerAdded.add(serializerFactory.javaClass)) return false
            kryo.addDefaultSerializer(type, serializerFactory)
            return true
        }
    }

    fun <T> copy(obj: T): T = synchronized(kryo) { kryo.copy(obj) }

    fun inject(target: Class<*>) {
        synchronized(kryo) {
            val messageSource = target.getDeclaredAnnotation(MessageSource::class.java)
            if (messageSource != null) inject(messageSource.value.java)
            for (type in target.declaredClasses)
                registerTree(type, type.getDeclaredAnnotation(Message::class.java) ?: continue)
            for (method in target.declaredMethods) {
                val annotations = method.parameterAnnotations
                val types = method.parameterTypes
                for (i in types.indices) for (annotation in annotations[i])
                    if (annotation is Message) registerTree(types[i], annotation)
            }
        }
    }

    fun register(messages: List<Pair<Class<*>, Message>>) {
        synchronized(kryo) {
            messages.forEach { registerTree(it.first, it.second) }
        }
    }



    override fun <T> readObject(path: String, type: Class<T>): T? =
        modLoader.findResource(path)?.let { synchronized(kryo) { kryo.readValue(it, type) } }

    override fun <T> readObjects(path: String, type: Class<T>): List<T> =
        modLoader.findResources(path).convert { synchronized(kryo) { kryo.readValue(it, type) } }

    override fun writeObject(path: String, obj: Any?): Resource? =
        modLoader.findResourceToWrite(path)?.also { synchronized(kryo) { kryo.writeValue(it, obj) } }

    override fun getExtension(): String = ".bin"

    override fun inject(target: Any) = inject(target.javaClass)



    private fun registerTree(type: Class<*>, message: Message) {
        val serializerFactoryType = message.factory.java
        if (serializerFactoryType !== SerializerFactory::class.java && defaultSerializerAdded.add(type))
            kryo.addDefaultSerializer(type, serializerFactoryType.getDeclaredConstructor().newInstance())

        if (message.full) {
            if (message.actualTypes.isEmpty())
                registerFull(type) else
                message.actualTypes.forEach { registerFull(it.java) }
        } else {
            if (message.actualTypes.isEmpty())
                registerNotFull(type) else
                message.actualTypes.forEach { registerNotFull(it.java) }
        }
    }

    private fun registerNotFull(type: Class<*>) {
        val componentType = type.componentType
        if (componentType == null) {
            if (isUseless(type) || !tryRegister(type)) return
            for (constructor in type.declaredConstructors) {
                val annotations = constructor.parameterAnnotations
                val types = constructor.parameterTypes
                for (i in types.indices) for (annotation in annotations[i])
                    if (annotation is Message) registerTree(types[i], annotation)
            }
            for (field in type.declaredFields)
                registerTree(field.type, field.getDeclaredAnnotation(Message::class.java) ?: continue)
        } else {
            if (tryRegister(type))
                registerNotFull(componentType)
        }
    }

    private fun registerFull(type: Class<*>) {
        val componentType = type.componentType
        if (componentType == null) {
            if (!isUseless(type) && tryRegister(type)) for (field in type.declaredFields)
                registerFull(field.type)
        } else {
            if (tryRegister(type))
                registerFull(componentType)
        }
    }

    private fun isUseless(type: Class<*>) = type === Any::class.java || type === String::class.java ||
            type.isPrimitive || type.isInterface || Modifier.isAbstract(type.modifiers)

    private fun tryRegister(type: Class<*>): Boolean {
        val nextId = kryo.nextRegistrationId
        return kryo.register(type).id == nextId
    }



    companion object {
        fun getDeclaredMessages(target: Class<*>): List<Pair<Class<*>, Message>> {
            val result = ObjectList<Pair<Class<*>, Message>>()
            val messageSource = target.getDeclaredAnnotation(MessageSource::class.java)
            if (messageSource != null) result.addAll(getDeclaredMessages(messageSource.value.java))
            for (type in target.declaredClasses)
                result.add(Pair(type, type.getDeclaredAnnotation(Message::class.java) ?: continue))
            for (method in target.declaredMethods) {
                val annotations = method.parameterAnnotations
                val types = method.parameterTypes
                for (i in types.indices) for (annotation in annotations[i])
                    if (annotation is Message) result.add(Pair(types[i], annotation))
            }
            return result
        }
    }
}