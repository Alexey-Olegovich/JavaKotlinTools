package alexey.tools.common.mods

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.misc.silentTry
import alexey.tools.common.resources.Resource

interface ModLoader {

    fun add(resource: Resource, id: String? = null): Mod
    fun add(module: Module, id: String? = null): Mod
    fun add(spec: String, id: String? = null): Mod
    fun add(type: String, path: String, id: String? = null): Mod
    fun add(resource: Resource, module: Module, id: String? = null): Mod
    fun add(mod: Mod): Mod

    fun remove(id: String): Mod? = throw UnsupportedOperationException("remove")
    fun clear() { throw UnsupportedOperationException("clear") }

    fun asMap(): Map<String, Mod> = emptyMap()

    // find local object ===============================================================================================

    @Suppress("unchecked_cast")
    fun <T> findObject(name: String, type: Class<T>): T? {
        forEachObject(name) { if (type.isAssignableFrom(it.javaClass)) return it as T }
        return null
    }

    @Suppress("unchecked_cast")
    fun <T> findObjects(name: String, type: Class<T>): Collection<T> =
        ObjectCollection<T>().apply { forEachObject(name) { if (type.isAssignableFrom(it.javaClass)) add(it as T) } }

    // find local resource =============================================================================================

    fun findResource(path: String): Resource? {
        forEachResource(path) { if (canRead()) return this }
        return null
    }

    fun findResources(path: String): Collection<Resource> =
        ObjectCollection<Resource>().apply { forEachResource(path) { if (canRead()) add(this) } }

    fun findResourceToWrite(path: String): Resource? {
        forEachResource(path) { if (canWrite()) return this }
        return null
    }

    // help methods ====================================================================================================

    private inline fun forEachResource(path: String, action: Resource.() -> Unit) {
        for (mod in asMap().values) if (mod.getResource().isValid())
            silentTry { mod.getLocalResource(path).action() }
    }

    private inline fun forEachObject(name: String, action: (Any) -> Unit) {
        for (mod in asMap().values) if (mod.getModule().isValid())
            silentTry { action(mod.getLocalObject(name)) }
    }
}