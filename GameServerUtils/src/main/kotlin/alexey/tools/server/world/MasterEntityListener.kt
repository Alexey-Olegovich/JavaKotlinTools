package alexey.tools.server.world

import alexey.tools.common.level.EntityGroup
import alexey.tools.common.level.EntityGroupListener

interface MasterEntityListener: EntitySubscriptionListener, EntityGroupListener {
    override fun onInsert(group: EntityGroup, entityId: Int) = inserted(entityId)
    override fun onRemove(group: EntityGroup, entityId: Int) = removed(entityId)
}