package alexey.tools.server.world

import com.artemis.EntitySubscription
import com.artemis.utils.IntBag

interface AbstractSubscriptionListener: EntitySubscription.SubscriptionListener {
    override fun removed(entities: IntBag) {}
    override fun inserted(entities: IntBag) {}
}