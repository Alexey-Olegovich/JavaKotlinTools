package alexey.tools.server.world

import com.artemis.BaseSystem
import com.artemis.World

open class DisabledSystem: BaseSystem() {
    override fun setWorld(world: World) {
        super.setWorld(world)
        isEnabled = false
    }
    override fun processSystem() {}
}