package alexey.tools.common.loaders

import alexey.tools.common.misc.PathUtils
import alexey.tools.common.resources.Resource

open class BaseGroup<T> protected constructor(protected val content: MutableMap<String, T?> = HashMap()):
    CachedPathGroup<T> {

    override fun readObject(path: String): T? =
        content[PathUtils.normalizePath(path)]

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