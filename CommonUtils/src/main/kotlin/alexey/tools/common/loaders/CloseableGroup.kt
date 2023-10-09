package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource
import java.io.Closeable
import java.util.function.Function

class CloseableGroup <T: Closeable> (loader: Function<String, T?>,
                                     concurrent: Boolean = true): DefaultGroup<T>(loader, concurrent) {

    override fun writeObject(path: String, obj: T?): Resource? {
        if (obj == null) return null
        content.put(path, obj)?.close()
        return Resource.NULL
    }

    override fun close() {
        content.values.forEach { it?.close() }
    }
}