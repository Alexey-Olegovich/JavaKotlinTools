package alexey.tools.common.loaders

import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function

class IndexGroupLoader <T> (private val loader: Function<String, T?>,
                            private val indexGroup: IndexGroup<T>): Function<String, T?> {

    private val nextId = AtomicInteger(0)

    override fun apply(path: String): T? {
        val value = loader.apply(path) ?: return null
        indexGroup.writeObject(nextId.getAndIncrement(), value)
        return value
    }
}