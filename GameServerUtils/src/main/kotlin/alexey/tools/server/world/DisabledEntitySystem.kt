package alexey.tools.server.world

import com.artemis.BaseEntitySystem
import com.artemis.World

open class DisabledEntitySystem: BaseEntitySystem() {
    override fun setWorld(world: World) {
        super.setWorld(world)
        isEnabled = false
    }
    override fun processSystem() {}
}