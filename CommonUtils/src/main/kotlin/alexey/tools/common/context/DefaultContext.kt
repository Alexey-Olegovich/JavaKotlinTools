package alexey.tools.common.context

import alexey.tools.common.misc.close
import java.io.Closeable
import java.util.IdentityHashMap
import java.util.function.Function

open class DefaultContext: Context {

    protected val data = IdentityHashMap<Class<*>, ImmutableContext.Container<*>>()



    override fun <T : Any> register(container: ImmutableContext.Container<T>) {
        data.put(container.getType(), container)?.close()
    }

    @Suppress("unchecked_cast")
    override fun <T> getOrNull(sharedObjectType: Class<T>): T? =
        data[sharedObjectType]?.get() as T?

    override fun contains(sharedObjectType: Class<*>): Boolean =
        data.containsKey(sharedObjectType)

    @Suppress("unchecked_cast")
    override fun <T : Any> obtain(sharedObjectType: Class<T>): T =
        data.computeIfAbsent(sharedObjectType, defaultComputeFunction).get() as T

    @Suppress("unchecked_cast")
    override fun <T : Closeable> obtain(sharedObjectType: Class<T>): T =
        data.computeIfAbsent(sharedObjectType, closeableComputeFunction).get() as T

    @Suppress("unchecked_cast")
    override fun <T: Any> remove(sharedObjectType: Class<T>): ImmutableContext.Container<T>? =
        data.remove(sharedObjectType) as ImmutableContext.Container<T>?

    override fun values(): Collection<ImmutableContext.Container<*>> = data.values

    override fun close() = data.values.close()



    companion object {
        private val defaultComputeFunction = Function<Class<*>, ImmutableContext.Container<*>> {
            RegistrableContext.DefaultContainer(it.getDeclaredConstructor().newInstance())
        }

        private val closeableComputeFunction = Function<Class<*>, ImmutableContext.Container<*>> {
            RegistrableContext.CloseableContainer(it.getDeclaredConstructor().newInstance() as Closeable)
        }
    }
}