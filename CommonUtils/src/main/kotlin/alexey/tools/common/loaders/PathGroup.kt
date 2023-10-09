package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource

interface PathGroup <T> {
    fun readObject(path: String): T? = null
    fun writeObject(path: String, obj: T?): Resource? = null
    fun obtainObject(path: String): T = readObject(path) ?: throw NullPointerException(path)

    companion object {
        val DEFAULT = object : PathGroup<Any> {}

        @Suppress("unchecked_cast")
        fun <T> default(): PathGroup<T> = DEFAULT as PathGroup<T>
    }
}