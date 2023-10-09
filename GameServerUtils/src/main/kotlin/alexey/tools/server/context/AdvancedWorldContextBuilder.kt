package alexey.tools.server.context

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.server.world.StaticEntitySystem

class AdvancedWorldContextBuilder: WorldContextBuilder() {

    private var staticSystems: ObjectCollection<StaticEntitySystem>? = null



    fun registerStaticSystem(system: StaticEntitySystem) {
        register(system)
        obtainStaticSystems().add(system)
    }

    fun registerStaticSystems(vararg systems: StaticEntitySystem) {
        val staticSystems = obtainStaticSystems()
        staticSystems.ensureAdd(systems.size)
        systems.forEach {
            register(it)
            staticSystems.unsafeAdd(it)
        }
    }

    fun registerStaticSystems(systems: Iterable<StaticEntitySystem>) {
        val staticSystems = obtainStaticSystems()
        systems.forEach {
            register(it)
            staticSystems.add(it)
        }
    }



    override fun build(): AdvancedWorldContext =
        AdvancedWorldContext(createWorldConfiguration(), createStaticSystems(), createContext())



    private fun obtainStaticSystems() =
        staticSystems ?: ObjectCollection<StaticEntitySystem>(8).also { staticSystems = it }

    private fun createStaticSystems() =
        staticSystems?.also { staticSystems = null } ?: emptyList<StaticEntitySystem>()
}