package alexey.tools.server.world

import com.artemis.utils.IntBag

interface OnRemoveSubscriptionListener: AbstractSubscriptionListener {

    override fun removed(entities: IntBag) { entities.forEach { removed(it) } }

    fun removed(entityId: Int)
}