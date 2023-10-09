package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource

interface IndexObjectIO: TypeGroup, IndexGroup<Any> {

    fun <T> readObject(index: Int, type: Class<T>): T? = null
    fun <T> readObjects(index: Int, type: Class<T>): List<T> {
        return listOf(readObject(index, type) ?: return emptyList())
    }

    fun <T: Any> readOrWriteDefault(index: Int, type: Class<T>): T {
        var result = readObject(index, type)
        if (result != null) return result
        result = type.getDeclaredConstructor().newInstance()
        writeObject(index, result)
        return result
    }

    fun <T> obtainObject(index: Int, type: Class<T>): T =
        readObject(index, type) ?: throw NullPointerException(index.toString())

    fun getExtension() = ""

    fun getContentType() = "application/octet-stream"



    override fun <T> readObject(type: Class<T>): T? = readObject(0, type)

    override fun writeObject(any: Any?): Resource? = writeObject(0, any)

    override fun writeObject(index: Int, obj: Any?): Resource? = null

    override fun readObject(index: Int): Any? = readObject(index, Any::class.java)
}