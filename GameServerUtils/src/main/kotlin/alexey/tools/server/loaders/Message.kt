package alexey.tools.server.loaders

import com.esotericsoftware.kryo.SerializerFactory
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class Message(val full: Boolean = false,
                         val factory: KClass<out SerializerFactory<*>> = SerializerFactory::class,
                         val actualTypes: Array<KClass<out Any>> = [])
