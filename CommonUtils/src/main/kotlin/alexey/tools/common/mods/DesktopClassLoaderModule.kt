package alexey.tools.common.mods

import alexey.tools.common.misc.*
import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.readBytes
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.function.Supplier

class DesktopClassLoaderModule (private val source: Resource,
                                private val dependencies: Iterable<Supplier<ClassLoaderModule>> = emptyList(),
                                parent: ClassLoader? = DesktopClassLoaderModule::class.java.classLoader):
    ClassLoader(parent), ClassLoaderModule {



    override fun getSource(): Resource = source

    override fun getLocalType(name: String): Class<*> =
        synchronized (getClassLoadingLock(name)) { findLoadedClass(name) ?: findClass(name) }

    override fun getGlobalType(name: String): Class<*> =
        synchronized (getClassLoadingLock(name)) { findLoadedClass(name) ?: findGlobalClass(name) }

    override fun getGlobalResource(name: String): Resource? {
        for (dependency in dependencies) {
            val resource = dependency.get().getGlobalResource(name)
            if (resource != null) return resource
        }
        return getLocalResource(name)
    }



    override fun loadClass(name: String, resolve: Boolean) =
        synchronized (getClassLoadingLock(name)) {
            (findLoadedClass(name) ?: loadType(name)).also { if (resolve) resolveClass(it) }
        }

    override fun getResource(name: String): URL? =
        parent?.getResource(name) ?: nullTry { getGlobalResource(name)?.toURL() }

    override fun getResourceAsStream(name: String): InputStream? =
        parent?.getResourceAsStream(name) ?: nullTry { getGlobalResource(name)?.getInputStream() }



    override fun findClass(name: String): Class<*> {
        val bytes = source.getResource(PathUtils.classNameToPath(name)).readBytes()
        defineBasicPackage(name)
        return defineClass(name, bytes, 0, bytes.size)
    }

    override fun findResource(name: String): URL? = getLocalResource(name)?.toURL()

    override fun findResources(name: String) = object : Enumeration<URL> {
        private var resource: Resource? = getLocalResource(name)
        override fun hasMoreElements() = resource != null
        override fun nextElement(): URL {
            val resource = resource ?: throw NoSuchElementException()
            this.resource = null
            return resource.toURL()
        }
    }



    private fun findGlobalClass(name: String): Class<*> {
        dependencies.forEach {
            val dependency = it.get()
            silentTry { return dependency.getGlobalType(name) }
        }
        return findClass(name)
    }

    private fun loadType(name: String): Class<*> {
        val parent = parent
        if (parent != null) silentTry { return parent.loadClass(name) }
        try {
            return findGlobalClass(name)
        } catch (e: Throwable) {
            throw ClassNotFoundException(name, e)
        }
    }

    private fun defineBasicPackage(name: String) {
        val i = name.lastIndexOf('.')
        if (i == -1) return
        val packageName = name.substring(0, i)
        if (getPackage(packageName) != null) return
        try {
            definePackage(packageName, null, null, null,
                null, null, null, null)
        } catch (_: IllegalArgumentException) {
            if (getPackage(packageName) == null)
                throw AssertionError("Cannot find package $packageName")
        }
    }



    companion object { init { registerAsParallelCapable() } }
}