package alexey.tools.server.world

import alexey.tools.common.misc.hasAnyMethod
import alexey.tools.common.misc.hasMethod
import com.artemis.BaseSystem
import com.artemis.EntitySubscription
import com.artemis.World
import com.artemis.utils.IntBag
import java.lang.reflect.Method

open class MasterSystem: BaseSystem(), EntitySubscriptionListener {

    private var entitySubscription: EntitySubscription? = null



    fun getEntitySubscription() = entitySubscription ?: throw NullPointerException()

    fun getEntitySubscriptionOrNull() = entitySubscription

    fun getEntityIds(): IntBag = getEntitySubscription().entities

    open fun implementsProcess() = implementsProcess(javaClass)

    open fun hasEntitySubscriptionListener() = hasEntitySubscriptionListener(javaClass)

    fun hasEntitySubscription() = entitySubscription != null

    open fun addSelf() {
        if (hasEntitySubscriptionListener()) getEntitySubscriptionOrNull()?.addSubscriptionListener(this)
    }



    override fun processSystem() {
        getEntitySubscription().entities.forEach { process(it) }
    }

    override fun setWorld(world: World) {
        super.setWorld(world)
        val type = javaClass
        val builder = type.aspectBuilder
        if (builder != null) entitySubscription = world.aspectSubscriptionManager.get(builder)
        if (!implementsProcess(type)) isEnabled = false
    }

    override fun inserted(entityId: Int) {}

    override fun removed(entityId: Int) {}



    protected open fun process(entityId: Int) {}



    companion object {

        val MASTER_SYSTEM_CLASS = MasterSystem::class.java
        val PROCESS_SYSTEM_METHOD: Method = MASTER_SYSTEM_CLASS.getDeclaredMethod("processSystem")
        val PROCESS_METHOD: Method = MASTER_SYSTEM_CLASS.getDeclaredMethod("process", Int::class.java)
        val ENTITY_SUBSCRIPTION_LISTENER_METHODS =
            OnInsertSubscriptionListener::class.java.declaredMethods +
            OnRemoveSubscriptionListener::class.java.declaredMethods



        fun implementsProcess(type: Class<out MasterSystem>) =
            type.hasMethod(PROCESS_METHOD, MASTER_SYSTEM_CLASS) ||
            type.hasMethod(PROCESS_SYSTEM_METHOD, MASTER_SYSTEM_CLASS)

        fun hasEntitySubscriptionListener(type: Class<out MasterSystem>) =
            type.hasAnyMethod(ENTITY_SUBSCRIPTION_LISTENER_METHODS, MASTER_SYSTEM_CLASS)
    }
}