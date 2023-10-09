package alexey.tools.server.world

import alexey.tools.common.collections.forEachInt
import alexey.tools.common.level.EntityGroup
import com.artemis.BaseSystem

abstract class BaseGroupSystem: BaseSystem() {

    var entityGroup = EntityGroup.DEFAULT
        protected set



    override fun processSystem() {
        entityGroup.forEachInt { process(it) }
    }

    protected abstract fun process(entityId: Int)
}