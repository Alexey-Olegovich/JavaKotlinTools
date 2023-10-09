package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource

interface ObjectIO: TypeGroup, PathGroup<Any> {

    fun <T> readObject(path: String, type: Class<T>): T? = null
    fun <T> readObjects(path: String, type: Class<T>): List<T> {
        return listOf(readObject(path, type) ?: return emptyList())
    }

    fun <T: Any> readOrWriteDefault(path: String, type: Class<T>): T {
        var result = readObject(path, type)
        if (result != null) return result
        result = type.getDeclaredConstructor().newInstance()
        writeObject(path, result)
        return result
    }

    fun <T> obtainObject(path: String, type: Class<T>): T =
        readObject(path, type) ?: throw NullPointerException(path)

    fun getExtension() = ""

    fun getContentType() = "application/octet-stream"



    override fun <T> readObject(type: Class<T>): T? = readObject("", type)

    override fun writeObject(any: Any?): Resource? = writeObject("", any)

    override fun writeObject(path: String, obj: Any?): Resource? = null

    override fun readObject(path: String): Any? = readObject(path, Any::class.java)
}