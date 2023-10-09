package alexey.tools.common.mods

import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.ResourceFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class DefaultModLoader(val resourceFactory: ResourceFactory<Resource> = ResourceFactory.newSafeInstance()): ModLoader {

    private val mods = ConcurrentHashMap<String, DefaultMod>()



    override fun add(resource: Resource, id: String?): Mod {
        val uuid = createId(id)
        val mod = DefaultMod(resource, Module.NULL, uuid)
        mods[uuid] = mod
        return mod
    }

    override fun add(module: Module, id: String?): Mod {
        val uuid = createId(id)
        val mod = DefaultMod(Resource.NULL, module, uuid)
        mods[uuid] = mod
        return mod
    }

    override fun add(spec: String, id: String?): Mod {
        return add(resourceFactory.create(spec), id)
    }

    override fun add(type: String, path: String, id: String?): Mod {
        return add(resourceFactory.create(type, path), id)
    }

    override fun add(resource: Resource, module: Module, id: String?): Mod {
        val uuid = createId(id)
        val mod = DefaultMod(resource, module, uuid)
        mods[uuid] = mod
        return mod
    }

    override fun add(mod: Mod): Mod {
        return add(mod.getResource(), mod.getModule(), mod.getId())
    }

    override fun remove(id: String): Mod? {
        return mods.remove(id)?.apply { invalidate() }
    }

    override fun clear() {
        mods.values.forEach { it.invalidate() }
        mods.clear()
    }



    private fun createId(id: String?): String =
        if (id.isNullOrEmpty()) UUID.randomUUID().toString() else id



    private class DefaultMod(private val resource: Resource,
                             private val module: Module,
                             private val id: String): GenericMod {

        @Volatile private var isValid = true

        fun invalidate() {
            isValid = false
        }

        override fun getId() = id
        override fun getModule() = module
        override fun getResource() = resource
        override fun isValid() = isValid
    }
}