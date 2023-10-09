package alexey.tools.server.context

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.context.ImmutableContext
import alexey.tools.common.misc.close
import com.artemis.BaseSystem
import com.artemis.SystemInvocationStrategy
import com.artemis.WorldConfiguration

open class WorldContextBuilder: GameRegistrableContext {

    private var config: WorldConfiguration? = null
    private var context: ObjectCollection<ImmutableContext.Container<*>>? = null



    override fun <T : Any> register(container: ImmutableContext.Container<T>) {
        obtainWorldConfiguration().register(container.getType().name, container.get())
        (context ?: ObjectCollection<ImmutableContext.Container<*>>(8).also { context = it }).add(container)
    }

    override fun close() { context?.close() }



    fun registerSystem(system: BaseSystem) {
        obtainWorldConfiguration().setSystem(system)
    }

    fun registerInvocationStrategy(invocationStrategy: SystemInvocationStrategy) {
        obtainWorldConfiguration().setInvocationStrategy(invocationStrategy)
    }

    fun delayDelete() {
        obtainWorldConfiguration().isAlwaysDelayComponentRemoval = true
    }

    fun registerSystems(vararg systems: BaseSystem) {
        val configuration = obtainWorldConfiguration()
        systems.forEach { configuration.setSystem(it) }
    }

    fun registerSystems(systems: Iterable<BaseSystem>) {
        val configuration = obtainWorldConfiguration()
        systems.forEach { configuration.setSystem(it) }
    }



    open fun build(): WorldContext =
        WorldContext(createWorldConfiguration(), createContext())



    private fun obtainWorldConfiguration() =
        config ?: WorldConfiguration().also { config = it }



    protected fun createContext() =
        context?.also { context = null } ?: emptyList<ImmutableContext.Container<*>>()

    protected fun createWorldConfiguration() =
        config?.also { config = null } ?: WorldConfiguration()
}