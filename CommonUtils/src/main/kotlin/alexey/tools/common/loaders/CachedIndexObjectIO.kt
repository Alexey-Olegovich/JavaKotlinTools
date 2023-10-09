package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource
import java.io.Closeable
import java.util.*

class CachedIndexObjectIO: IndexObjectIO, Closeable {

    private val groups = IdentityHashMap<Class<*>, CachedIndexGroup<*>>()



    override fun writeObject(index: Int, obj: Any?): Resource? =
        if (obj == null) null else getGroupOrNull(obj.javaClass)?.writeObject(index, obj)

    override fun <T> readObject(index: Int, type: Class<T>): T? =
        getGroupOrNull(type)?.readObject(index)



    @Suppress("UNCHECKED_CAST")
    fun <T> getGroupOrNull(type: Class<T>): CachedIndexGroup<T>? = groups[type] as CachedIndexGroup<T>?

    fun <T> getGroup(type: Class<T>): CachedIndexGroup<T> =
        getGroupOrNull(type) ?: throw NullPointerException(type.toString())

    fun <T> setGroup(type: Class<T>, group: CachedIndexGroup<T>) { groups.put(type, group)?.close() }

    inline fun <reified T> setGroup(group: CachedIndexGroup<T>) = setGroup(T::class.java, group)

    inline fun <T> obtainGroup(type: Class<T>, computeFunction: () -> CachedIndexGroup<T>): CachedIndexGroup<T> {
        var group = getGroupOrNull(type)
        if (group == null) {
            group = computeFunction()
            setGroup(type, group)
        }
        return group
    }



    override fun close() { groups.values.forEach { it.close() } }

    fun asMap(): Map<Class<*>, CachedIndexGroup<*>> = groups

    fun clear() { groups.values.forEach { it.clear() } }
}