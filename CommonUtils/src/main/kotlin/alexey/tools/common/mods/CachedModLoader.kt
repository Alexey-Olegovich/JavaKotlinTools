package alexey.tools.common.mods

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.silentFastForEach
import alexey.tools.common.resources.Resource
import java.util.concurrent.locks.ReentrantReadWriteLock

class CachedModLoader(private val modLoader: ModLoader): ModLoader {

    private val modsWithModules = ObjectList<Mod>()
    private val modsWithResources = ObjectList<Mod>()

    private val lock = ReentrantReadWriteLock()



    init { modLoader.asMap().values.forEach { put0(it) } }



    override fun add(resource: Resource, id: String?): Mod =
        put(modLoader.add(resource, id))

    override fun add(module: Module, id: String?): Mod =
        put(modLoader.add(module, id))

    override fun add(spec: String, id: String?): Mod =
        put(modLoader.add(spec, id))

    override fun add(type: String, path: String, id: String?): Mod =
        put(modLoader.add(type, path, id))

    override fun add(resource: Resource, module: Module, id: String?): Mod =
        put(modLoader.add(resource, module, id))

    override fun add(mod: Mod): Mod =
        put(modLoader.add(mod))

    override fun clear() {
        modLoader.clear()
        update()
    }

    override fun remove(id: String): Mod? = remove(modLoader.remove(id))

    override fun asMap(): Map<String, Mod> = modLoader.asMap()

    fun update() {
        lock.writeLock().lock()
        val r = modsWithResources.iterator()
        while (r.hasNext()) if (!r.next().isValid()) r.remove()
        val m = modsWithModules.iterator()
        while (m.hasNext()) if (!m.next().isValid()) m.remove()
        lock.writeLock().unlock()
    }



    @Suppress("unchecked_cast")
    override fun <T> findObject(name: String, type: Class<T>): T? {
        forEachObject(name) { if (type.isAssignableFrom(it.javaClass)) return it as T }
        return null
    }

    @Suppress("unchecked_cast")
    override fun <T> findObjects(name: String, type: Class<T>): Collection<T> =
        ObjectCollection<T>().apply { forEachObject(name) { if (type.isAssignableFrom(it.javaClass)) add(it as T) } }



    override fun findResource(path: String): Resource? {
        forEachResource(path) { if (it.canRead()) return it }
        return null
    }

    override fun findResourceToWrite(path: String): Resource? {
        forEachResource(path) { if (it.canWrite()) return it }
        return null
    }

    override fun findResources(path: String): Collection<Resource> =
        ObjectCollection<Resource>().apply { forEachResource(path) { if (it.canRead()) add(it) } }



    private inline fun forEachResource(path: String, action: (Resource) -> Unit) {
        lock.readLock().lock()
        modsWithResources.silentFastForEach { action(it.getLocalResource(path)) }
        lock.readLock().unlock()
    }

    private inline fun forEachObject(name: String, action: (Any) -> Unit) {
        lock.readLock().lock()
        modsWithModules.silentFastForEach { action(it.getLocalObject(name)) }
        lock.readLock().unlock()
    }



    private fun put0(mod: Mod) {
        if (mod.getModule().isValid()) modsWithModules.add(mod)
        if (mod.getResource().isValid()) modsWithResources.add(mod)
    }

    private fun remove(mod: Mod?): Mod? {
        if (mod == null) return null
        lock.writeLock().lock()
        if (mod.getModule().isValid()) modsWithModules.removeReference(mod)
        if (mod.getResource().isValid()) modsWithResources.removeReference(mod)
        lock.writeLock().unlock()
        return mod
    }

    private fun put(mod: Mod): Mod {
        lock.writeLock().lock()
        if (mod.isValid()) put0(mod)
        lock.writeLock().unlock()
        return mod
    }
}