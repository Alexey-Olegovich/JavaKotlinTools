package alexey.tools.server.context

import alexey.tools.common.context.ImmutableContext
import alexey.tools.common.misc.close
import alexey.tools.server.core.Processor
import alexey.tools.server.world.addSubscriptionListeners
import com.artemis.World
import com.artemis.WorldConfiguration

open class WorldContext(private var config: WorldConfiguration? = WorldConfiguration(),
                        private val context: Collection<ImmutableContext.Container<*>> = emptyList()): Processor, ImmutableContext {

    private var world: World? = null



    override fun <T> getOrNull(sharedObjectType: Class<T>): T? = world?.getRegistered(sharedObjectType)

    override fun values(): Collection<ImmutableContext.Container<*>> = context



    override fun enable() {
        if (config == null) return
        world = createWorld(config)
        config = null
    }

    fun run(deltaTime: Float) {
        world?.run { setDelta(deltaTime); process() }
    }

    override fun run() {
        world?.process()
    }

    override fun close() {
        try {
            world?.dispose()
            world = null
        } finally {
            context.close()
        }
    }



    protected open fun createWorld(config: WorldConfiguration?): World {
        val world = World(config)
        val subscriptionManager = world.aspectSubscriptionManager
        world.systems.forEach { subscriptionManager.addSubscriptionListeners(it) }
        return world
    }
}