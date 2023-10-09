package alexey.tools.common.loaders

import java.util.function.BiConsumer
import java.util.function.Function

class ListenableLoader <T> (private val loader: Function<String, T?>,
                            private val listener: BiConsumer<String, T>): Function<String, T?> {

    override fun apply(path: String): T? {
        val value = loader.apply(path) ?: return null
        listener.accept(path, value)
        return value
    }
}