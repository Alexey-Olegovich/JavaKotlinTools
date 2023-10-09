package alexey.tools.common.context

import alexey.tools.common.misc.Injector
import java.io.Closeable
import java.util.function.Supplier

interface ImmutableContext: Closeable, Injector {
    fun <T> getOrNull(sharedObjectType: Class<T>): T? =
        throw UnsupportedOperationException("getOrNull")

    fun contains(sharedObjectType: Class<*>): Boolean = getOrNull(sharedObjectType) != null

    fun <T> get(sharedObjectType: Class<T>): T = getOrNull(sharedObjectType)
        ?: throw NullPointerException("Shared object ($sharedObjectType) not found!")

    fun values(): Collection<Container<*>> = throw UnsupportedOperationException("values")

    @Suppress("unchecked_cast")
    fun <T> newInstance(type: Class<T>): T {
        val constructor = type.declaredConstructors[0]
        val types = constructor.parameterTypes
        val values = arrayOfNulls<Any>(types.size)
        for (i in types.indices) values[i] = get(types[i])
        constructor.isAccessible = true
        return constructor.newInstance(*values) as T
    }

    override fun inject(target: Any) {
        val fields = target.javaClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            if (field.get(target) != null) continue
            field.set(target, getOrNull(field.type) ?: continue)
        }
    }

    fun injectAll(target: Any) {
        val fields = target.javaClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            if (field.get(target) != null) continue
            field.set(target, get(field.type))
        }
    }

    fun remoteCopy(): Context = DefaultContext().apply { registerCopy(this) }



    interface Container <T: Any>: Closeable, Supplier<T> {
        fun getType(): Class<in T> = get().javaClass
        override fun close() {}
    }
}