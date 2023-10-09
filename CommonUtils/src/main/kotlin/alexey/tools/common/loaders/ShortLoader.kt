package alexey.tools.common.loaders

import java.util.function.Function

class ShortLoader <T> (private val loader: Function<String, T?>,
                       private val extension: String,
                       private val root: String): Function<String, T?> {

    override fun apply(path: String): T? =
        loader.apply(buildString(path.length + root.length + extension.length + 2) {
            append(root)
            append('/')
            append(path)
            append('.')
            append(extension)
        })
}