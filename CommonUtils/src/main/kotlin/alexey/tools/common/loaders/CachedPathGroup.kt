package alexey.tools.common.loaders

import java.io.Closeable

interface CachedPathGroup <T>: PathGroup<T>, Closeable {
    fun clear() {}
    fun asMap(): Map<String, T?> = emptyMap()
    override fun close() {}



    companion object {
        val DEFAULT = object : CachedPathGroup<Any> {}

        @Suppress("unchecked_cast")
        fun <T> default(): CachedPathGroup<T> = DEFAULT as CachedPathGroup<T>
    }
}