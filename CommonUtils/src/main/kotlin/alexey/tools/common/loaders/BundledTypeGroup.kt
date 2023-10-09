package alexey.tools.common.loaders

import alexey.tools.common.misc.appendLowercaseWords
import java.util.*

class BundledTypeGroup(val objectIO: ObjectIO,
                       val root: String,
                       val removePostfix: Int = 5): TypeGroup {

    private val data = IdentityHashMap<Class<*>, Any>()



    @Suppress("unchecked_cast")
    override fun <T> readObject(type: Class<T>): T {
        var obj = data[type]
        if (obj != null) return obj as T
        val simpleName = type.simpleName
        val extension = objectIO.getExtension()
        val fullPath = buildString(root.length + simpleName.length + extension.length) {
            append(root)
            append('/')
            appendLowercaseWords(simpleName, removePostfix)
            append(extension)
        }
        obj = objectIO.readObject(fullPath, type) ?: type.getDeclaredConstructor().newInstance()
        data[type] = obj
        return obj as T
    }
}