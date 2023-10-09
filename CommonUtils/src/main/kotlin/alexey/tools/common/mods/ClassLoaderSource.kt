package alexey.tools.common.mods

import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.ResourceBase
import alexey.tools.common.resources.URLResource

open class ClassLoaderSource(val classLoader: ClassLoader = ClassLoaderSource::class.java.classLoader) : ClassLoaderModule, ResourceBase() {
    override fun canRead(): Boolean = false
    override fun canWrite(): Boolean = false
    override fun getPath(): String = classLoader.toString()
    override fun getResourceType(): String = "loader-class"
    override fun isValid(): Boolean  = true
    override fun getSource(): Resource = this
    override fun getLocalType(name: String): Class<*> = classLoader.loadClass(name)

    override fun getLocalResource(name: String): Resource? {
        return URLResource(classLoader.getResource(name) ?: return null)
    }

    override fun getResource(relativePath: String): Resource {
        return URLResource(classLoader.getResource(relativePath) ?: return Resource.NULL)
    }
}