package alexey.tools.server.loaders

import alexey.tools.common.loaders.DefaultGroup
import alexey.tools.common.resources.Resource
import com.badlogic.gdx.utils.Disposable
import java.util.function.Function

open class DisposableGroup <T: Disposable> (loader: Function<String, T?>,
                                            concurrent: Boolean = true): DefaultGroup<T>(loader, concurrent) {

    override fun writeObject(path: String, obj: T?): Resource? {
        if (obj == null) return null
        content.put(path, obj)?.dispose()
        return Resource.NULL
    }

    override fun close() {
        content.values.forEach { it?.dispose() }
    }
}