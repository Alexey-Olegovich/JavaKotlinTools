package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource

interface TypeGroup {
    fun <T> readObject(type: Class<T>): T? = null
    fun writeObject(any: Any?): Resource? = null
    fun <T> obtainObject(type: Class<T>): T = readObject(type) ?: throw NullPointerException(type.toString())
}