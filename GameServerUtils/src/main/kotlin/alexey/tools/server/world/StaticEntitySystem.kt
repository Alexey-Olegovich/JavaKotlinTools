package alexey.tools.server.world

import com.artemis.BaseEntitySystem
import com.artemis.World
import com.badlogic.gdx.utils.Disposable

open class StaticEntitySystem: BaseEntitySystem(), Disposable {
    override fun checkProcessing(): Boolean = false
    override fun processSystem() {}
    public override fun setWorld(world: World) = super.setWorld(world)
    public override fun initialize() {}
    override fun dispose() {}
}