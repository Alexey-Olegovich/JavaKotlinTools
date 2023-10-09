package alexey.tools.common.context

import java.io.Closeable
import java.util.function.Consumer

interface RegistrableContext: Closeable {
    fun <T: Any> register(container: ImmutableContext.Container<T>)

    fun <T: Any> registerCopy(container: ImmutableContext.Container<T>) =
        register(DefaultContainer(container.get(), container.getType()))

    fun <T: Any> register(sharedObject: T, sharedObjectType: Class<in T>, closeFunction: Consumer<T>) =
        register(DefaultContainer(sharedObject, sharedObjectType, closeFunction))

    fun <T: Any> register(sharedObject: T, sharedObjectType: Class<in T>) =
        register(DefaultContainer(sharedObject, sharedObjectType))

    fun <T: Any> register(sharedObject: T) =
        register(sharedObject, sharedObject.javaClass)

    fun <T: Any> register(sharedObject: T, closeFunction: Consumer<T>) =
        register(sharedObject, sharedObject.javaClass, closeFunction)

    fun <T: Closeable> register(sharedObject: T, sharedObjectType: Class<in T> = sharedObject.javaClass) =
        register(CloseableContainer(sharedObject, sharedObjectType))

    fun registerCopy(containers: Iterable<ImmutableContext.Container<*>>) =
        containers.forEach { registerCopy(it) }

    fun registerCopy(context: ImmutableContext) =
        registerCopy(context.values())



    class CloseableContainer <T: Closeable>(private val value: T,
                                            private val type: Class<in T> = value.javaClass): ImmutableContext.Container<T> {
        override fun get() = value
        override fun getType() = type
        override fun close() { value.close() }
    }

    class DefaultContainer <T: Any> (private val value: T,
                                     private val type: Class<in T> = value.javaClass,
                                     private val closeFunction: Consumer<T>? = null): ImmutableContext.Container<T> {
        override fun get() = value
        override fun getType() = type
        override fun close() { closeFunction?.accept(value) }
    }
}