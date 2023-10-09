package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

open class CachedObjectIO: Closeable, ObjectIO {

    protected val groups = ConcurrentHashMap<Class<*>, CachedPathGroup<*>>()



    override fun writeObject(path: String, obj: Any?): Resource? =
        if (obj == null) null else getGroupOrNull(obj.javaClass)?.writeObject(path, obj)

    override fun <T> readObject(path: String, type: Class<T>): T? =
        getGroupOrNull(type)?.readObject(path)



    @Suppress("UNCHECKED_CAST")
    open fun <T> getGroupOrNull(type: Class<T>): CachedPathGroup<T>? = groups[type] as CachedPathGroup<T>?

    fun <T> getGroup(type: Class<T>): CachedPathGroup<T> =
        getGroupOrNull(type) ?: throw NullPointerException(type.toString())

    open fun <T> setGroup(type: Class<T>, group: CachedPathGroup<T>) { groups.put(type, group)?.close() }

    inline fun <reified T> setGroup(group: CachedPathGroup<T>) = setGroup(T::class.java, group)

    inline fun <T> obtainGroup(type: Class<T>, computeFunction: () -> CachedPathGroup<T>): CachedPathGroup<T> {
        var group = getGroupOrNull(type)
        if (group == null) {
            group = computeFunction()
            setGroup(type, group)
        }
        return group
    }

    fun <T> obtainGroup(type: Class<T>, objectIO: ObjectIO): CachedPathGroup<T> =
        obtainGroup(type) { ObjectIOGroup(objectIO, type) }



    override fun close() { groups.values.forEach { it.close() } }

    open fun asMap(): Map<Class<*>, CachedPathGroup<*>> = groups

    open fun clear() { groups.values.forEach { it.clear() } }
}