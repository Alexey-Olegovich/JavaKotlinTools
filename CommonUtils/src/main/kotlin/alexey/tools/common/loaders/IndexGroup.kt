package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource

interface IndexGroup <T> {
    fun readObject(index: Int): T? = null
    fun writeObject(index: Int, obj: T?): Resource? = null
    fun obtainObject(index: Int): T = readObject(index) ?: throw NullPointerException(index.toString())

    companion object {
        val DEFAULT = object : IndexGroup<Any> {}

        @Suppress("unchecked_cast")
        fun <T> default() = DEFAULT as IndexGroup<T>
    }
}