package alexey.tools.common.loaders

import alexey.tools.common.misc.add
import alexey.tools.common.misc.appendLowercaseWords
import alexey.tools.common.resources.Resource

class BundledObjectIO(val objectIO: ObjectIO,
                      val root: String,
                      val removePostfix: Int = 5): ObjectIO {

    private val data = HashMap<Pair<String, Class<*>>, Any?>()



    @Suppress("unchecked_cast")
    override fun <T> readObject(path: String, type: Class<T>): T? =
        readObject(Pair(path, type)) as T?

    @Suppress("unchecked_cast")
    override fun <T> obtainObject(path: String, type: Class<T>): T {
        val key = Pair(path, type)
        var obj = readObject(key)
        if (obj == null) {
            obj = type.getDeclaredConstructor().newInstance()
            data[key] = obj
        }
        return obj as T
    }

    override fun writeObject(path: String, obj: Any?): Resource? {
        if (obj == null) return null
        data[Pair(path, obj.javaClass)] = obj
        return Resource.NULL
    }

    override fun getExtension(): String = objectIO.getExtension()

    override fun getContentType(): String = objectIO.getContentType()



    private fun readObject(key: Pair<String, Class<*>>): Any? {
        var obj = data[key]
        if (obj != null) return obj
        val simpleName = key.second.simpleName
        val extension = getExtension()
        val fullPath = buildString(root.length + simpleName.length + extension.length) {
            append(root)
            append('/')
            appendLowercaseWords(key.first add simpleName, removePostfix)
            append(extension)
        }
        obj = objectIO.readObject(fullPath, key.second)
        if (obj != null) data[key] = obj
        return obj
    }
}