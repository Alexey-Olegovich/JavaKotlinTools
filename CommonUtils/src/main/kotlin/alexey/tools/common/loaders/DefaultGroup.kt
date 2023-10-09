package alexey.tools.common.loaders

import alexey.tools.common.misc.PathUtils
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

open class DefaultGroup <T> private constructor (protected val loader: Function<String, T?>,
                                                 content: MutableMap<String, T?>): BaseGroup<T>(content) {

    constructor(loader: Function<String, T?>, concurrent: Boolean = true):
            this(loader, if (concurrent) ConcurrentHashMap() else HashMap())



    override fun readObject(path: String): T? {
        return content.computeIfAbsent(PathUtils.normalizePath(path), loader)
    }
}