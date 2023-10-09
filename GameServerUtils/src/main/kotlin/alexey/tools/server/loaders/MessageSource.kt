package alexey.tools.server.loaders

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class MessageSource(val value: KClass<out Any> = Any::class)
