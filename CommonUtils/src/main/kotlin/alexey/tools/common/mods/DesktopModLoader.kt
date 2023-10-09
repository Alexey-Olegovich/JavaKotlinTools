package alexey.tools.common.mods

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.misc.close
import alexey.tools.common.misc.silentTry
import alexey.tools.common.misc.split
import alexey.tools.common.resources.*
import java.io.Closeable
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.Supplier

class DesktopModLoader(val resourceFactory: ResourceFactory<ResourceRoot> = ResourceFactory.newDefaultInstance(),
                       val configPath: String = "mod.ini"): ModLoader, Closeable {

    private val mods = ConcurrentHashMap<String, DesktopMod>()



    override fun add(resource: Resource, id: String?): Mod =
        add(create(id, ResourceRootWrapper(resource)))

    override fun add(module: Module, id: String?): Mod =
        add(create(id, ClassLoaderModuleWrapper(module), ResourceRoot.NULL))

    override fun add(spec: String, id: String?): Mod =
        add(create(id, resourceFactory.create(spec)))

    override fun add(type: String, path: String, id: String?): Mod =
        add(create(id, resourceFactory.create(type, path)))

    override fun add(resource: Resource, module: Module, id: String?): Mod =
        add(create(id, ClassLoaderModuleWrapper(module, resource), ResourceRootWrapper(resource)))

    override fun add(mod: Mod): Mod = add(create(mod.getId(),
        ClassLoaderModuleWrapper(mod.getModule(), mod.getResource()), ResourceRootWrapper(mod.getResource())
    ))

    override fun remove(id: String): Mod? = mods.remove(id)?.also { it.close() }

    override fun clear() { close(); mods.clear() }

    override fun close() = mods.values.close()

    override fun asMap(): Map<String, Mod> = mods



    private fun create(id: String?, config: ModConfig, resource: ResourceRoot): DesktopMod =
        when (config.getType()) {
            "lib" -> create(id, DesktopClassLoaderModule(resource, config.getDependencies()), ResourceRoot.NULL)
            "all" -> create(id, DesktopClassLoaderModule(resource, config.getDependencies()), resource)
            else -> create(id, ClassLoaderModule.NULL, resource)
        }

    private fun create(id: String?, module: ClassLoaderModule, resource: ResourceRoot): DesktopMod =
        if (id == null)
            GenericDesktopMod(resource, module) else
            DesktopMod(id, resource, module)

    private fun create(id: String?, resource: ResourceRoot): DesktopMod =
        readConfig(resource).let { create(id ?: it.getId(), it, resource) }

    private fun add(mod: DesktopMod): Mod {
        mods.put(mod.getId(), mod)?.close()
        return mod
    }

    private fun readConfig(resource: Resource): ModConfig {
        val configResource = resource.getResource(configPath)
        val result = ConfigParser()
        if (!configResource.isValid()) return result
        silentTry { result.readConfig(configResource) }
        return result
    }



    private inner class ModSupplier(private val id: String): Supplier<ClassLoaderModule> {

        private var reference = nullReference



        override fun get(): ClassLoaderModule {
            synchronized(this) {
                var mod = reference.get()
                if (mod != null) return mod
                mod = (mods[id] ?: error("Dependency '$id' not found!")).getModule()
                reference = WeakReference(mod)
                return mod
            }
        }
    }

    private inner class ConfigParser: BiConsumer<String, String>, ModConfig {

        private var type = ""
        private var id: String? = null
        private var dependencies: Collection<Supplier<ClassLoaderModule>> = emptyList()



        override fun accept(t: String, u: String) {
            if (t == "type") type = u else {
                id = t
                dependencies = if (u.isEmpty()) emptyList() else parseDependencies(u)
            }
        }

        private fun parseDependencies(dependenciesArray: String): Collection<Supplier<ClassLoaderModule>> {
            val dependenciesIds = dependenciesArray.split(',')
            val dependencies = ObjectCollection<Supplier<ClassLoaderModule>>(dependenciesIds.size)
            dependenciesIds.forEach { if (it.isNotEmpty()) dependencies.unsafeAdd(ModSupplier(it)) }
            return dependencies
        }

        override fun getId() = id

        override fun getDependencies(): Collection<Supplier<ClassLoaderModule>> = dependencies

        override fun getType(): String = type
    }



    private open class DesktopMod(private val id: String,
                                  private val resource: ResourceRoot,
                                  private val module: ClassLoaderModule
    ): CloseableMod {

        @Volatile private var isValid = true

        override fun getId(): String = id
        override fun getModule(): ClassLoaderModule = module
        override fun getResource(): ResourceRoot = resource
        override fun isValid(): Boolean = isValid
        override fun close() { super.close(); isValid = false }
    }

    private class GenericDesktopMod(resource: ResourceRoot,
                                    module: ClassLoaderModule
    ):
        DesktopMod(UUID.randomUUID().toString(), resource, module), GenericMod



    private interface ModConfig {
        fun getType(): String
        fun getId(): String?
        fun getDependencies(): Collection<Supplier<ClassLoaderModule>>
    }



    companion object {
        private val nullReference = WeakReference<ClassLoaderModule>(null)
    }
}