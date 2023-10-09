package alexey.tools.common.resources

import alexey.tools.common.mods.ClassLoaderSource
import java.util.concurrent.ConcurrentHashMap

class ResourceFactory<T: Resource> {

    private val converters = ConcurrentHashMap<String, (String) -> T>()
    @Volatile var separator = ':'



    fun create(spec: String): T {
        val i = spec.indexOf(separator)
        if (i == -1) return create(spec, "")
        val type = spec.substring(0, i)
        return create(type, spec.substring(i + 1))
    }

    fun create(type: String, path: String): T
        = converters[type]?.invoke(path) ?: throw NoSuchElementException("Converter not found ($type)!")

    fun setConverter(type: String, converter: (String) -> T) {
        converters[type] = converter
    }



    companion object {
        fun newDefaultInstance(): ResourceFactory<ResourceRoot> {
            val result = ResourceFactory<ResourceRoot>()
            result.setConverter("file") { ResourceRootWrapper(FileResource(it)) }
            result.setConverter("url") { ResourceRootWrapper(URLResource(it)) }
            result.setConverter("file-zip") { ZipFileResource(it) }
            result.setConverter("loader-class") { ResourceRootWrapper(ClassLoaderSource()) }
            result.setConverter("remote") { TCPRemoteResource.newInstance(it) }
            return result
        }

        fun newSafeInstance(): ResourceFactory<Resource> {
            val result = ResourceFactory<Resource>()
            result.setConverter("file") { FileResource(it) }
            result.setConverter("url") { URLResource(it) }
            return result
        }
    }
}