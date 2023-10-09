package alexey.tools.common.loaders

import java.io.Closeable

interface CachedIndexGroup<T>: IndexGroup<T>, Closeable {
    fun clear() {}
    fun asList(): List<T?> = emptyList()
    override fun close() {}



    companion object {
        val DEFAULT = object : CachedIndexGroup<Any> {}

        @Suppress("unchecked_cast")
        fun <T> default(): CachedIndexGroup<T> = DEFAULT as CachedIndexGroup<T>
    }
}