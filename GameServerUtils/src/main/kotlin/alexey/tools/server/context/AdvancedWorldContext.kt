package alexey.tools.server.context

import alexey.tools.common.context.ImmutableContext
import alexey.tools.server.world.StaticEntitySystem
import alexey.tools.server.world.addSubscriptionListeners
import com.artemis.World
import com.artemis.WorldConfiguration

class AdvancedWorldContext(config: WorldConfiguration? = WorldConfiguration(),
                           private val staticSystems: Collection<StaticEntitySystem> = emptyList(),
                           context: Collection<ImmutableContext.Container<*>> = emptyList()): WorldContext(config, context) {

    override fun createWorld(config: WorldConfiguration?): World {
        val world = World(config)
        val subscriptionManager = world.aspectSubscriptionManager
        world.systems.forEach {
            subscriptionManager.addSubscriptionListeners(it)
        }
        staticSystems.forEach {
            it.setWorld(world)
            inject(it)
            it.initialize()
            subscriptionManager.addSubscriptionListeners(it)
        }
        return world
    }
}