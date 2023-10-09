package alexey.tools.server.world

import com.artemis.utils.IntBag

interface OnInsertSubscriptionListener: AbstractSubscriptionListener {

    override fun inserted(entities: IntBag) { entities.forEach { inserted(it) } }

    fun inserted(entityId: Int)
}