package alexey.tools.server.world

import com.artemis.World

open class DisabledGroupSystem: BaseGroupSystem() {
    override fun setWorld(world: World) {
        super.setWorld(world)
        isEnabled = false
    }
    override fun process(entityId: Int) {}
}