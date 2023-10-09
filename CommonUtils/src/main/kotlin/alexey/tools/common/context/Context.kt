package alexey.tools.common.context

import java.io.Closeable

interface Context: RegistrableContext, ImmutableContext {
    fun <T: Any> remove(sharedObjectType: Class<T>): ImmutableContext.Container<T>?

    fun <T: Any> obtain(sharedObjectType: Class<T>): T =
        obtain(sharedObjectType) { sharedObjectType.getDeclaredConstructor().newInstance() }

    fun <T: Closeable> obtain(sharedObjectType: Class<T>): T =
        obtain(sharedObjectType) { sharedObjectType.getDeclaredConstructor().newInstance() }

    fun <T: Any> obtain(sharedObjectType: Class<out T>, sharedObjectAsType: Class<T>): T =
        obtain(sharedObjectAsType) { sharedObjectType.getDeclaredConstructor().newInstance() }

    fun <T: Closeable> obtain(sharedObjectType: Class<out T>, sharedObjectAsType: Class<T>): T =
        obtain(sharedObjectAsType) { sharedObjectType.getDeclaredConstructor().newInstance() }

    fun <T: Any> registerIfAbsent(sharedObject: T): T? =
        registerIfAbsent(sharedObject, sharedObject.javaClass)

    fun <T: Closeable> registerIfAbsent(sharedObject: T): T? =
        registerIfAbsent(sharedObject, sharedObject.javaClass)


    fun <T: Any> registerIfAbsent(sharedObject: T, sharedObjectType: Class<T>): T? {
        val s = getOrNull(sharedObjectType)
        if (s != null) return s
        register(sharedObject)
        return null
    }

    fun <T: Closeable> registerIfAbsent(sharedObject: T, sharedObjectType: Class<T>): T? {
        val s = getOrNull(sharedObjectType)
        if (s != null) return s
        register(sharedObject)
        return null
    }
}