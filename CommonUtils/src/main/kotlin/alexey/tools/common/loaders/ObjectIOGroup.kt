package alexey.tools.common.loaders

import alexey.tools.common.misc.PathUtils
import alexey.tools.common.resources.Resource
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class ObjectIOGroup <T> (private val objectIO: ObjectIO,
                         type: Class<T>): CachedPathGroup<T> {

    private val content = ConcurrentHashMap<String, T?>()
    private val loader = Function<String, T?> { objectIO.readObject(it, type) }



    override fun readObject(path: String): T? {
        return content.computeIfAbsent(PathUtils.normalizePath(path), loader)
    }

    override fun writeObject(path: String, obj: T?): Resource? {
        if (obj == null) return null
        content[path] = obj
        return Resource.NULL
    }

    override fun clear() {
        close()
        content.clear()
    }

    override fun asMap(): Map<String, T?> = content
}