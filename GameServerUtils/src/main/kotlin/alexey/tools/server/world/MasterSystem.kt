package alexey.tools.server.world

import alexey.tools.common.collections.forEachInt
import alexey.tools.common.level.*
import alexey.tools.common.misc.hasAnyMethod
import alexey.tools.common.misc.hasMethod
import com.artemis.BaseSystem
import com.artemis.EntitySubscription
import com.artemis.World
import com.artemis.utils.IntBag

open class MasterSystem: BaseSystem(), MasterEntityListener {

    var entityGroup = EntityGroup.DEFAULT
        protected set
    private var entitySubscription: EntitySubscription? = null



    fun getEntitySubscription() = entitySubscription ?: throw NullPointerException()

    fun getEntitySubscriptionOrNull() = entitySubscription

    fun getEntityIds(): IntBag = getEntitySubscription().entities

    fun implementsProcess() = implementsProcess(javaClass)

    fun hasEntityGroupListener() = hasEntityGroupListener(javaClass)

    fun hasEntityListener() = hasEntityListener(javaClass)

    fun hasEntitySubscriptionListener() = hasEntitySubscriptionListener(javaClass)

    fun hasEntities() = hasEntityGroup() || hasEntitySubscription()

    fun hasEntityGroup() = entityGroup !== EntityGroup.DEFAULT

    fun hasEntitySubscription() = entitySubscription != null

    fun addListener(listener: MasterEntityListener) {
        if (entityGroup === EntityGroup.DEFAULT)
            getEntitySubscription().addSubscriptionListener(listener) else
            entityGroup.addListener(listener)
    }

    fun addSelf() {
        if (hasEntities() && hasEntitySubscriptionListener()) addListener(this)
    }



    override fun processSystem() {
        if (entityGroup === EntityGroup.DEFAULT)
            getEntitySubscription().entities.forEach { process(it) } else
            entityGroup.forEachInt { process(it) }
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

        private val MASTER_SYSTEM_CLASS = MasterSystem::class.java
        private val PROCESS_SYSTEM_METHOD = MASTER_SYSTEM_CLASS.getDeclaredMethod("processSystem")
        private val PROCESS_METHOD = MASTER_SYSTEM_CLASS.getDeclaredMethod("process", Int::class.java)
        private val ENTITY_SUBSCRIPTION_LISTENER_METHODS =
            OnInsertSubscriptionListener::class.java.declaredMethods +
            OnRemoveSubscriptionListener::class.java.declaredMethods
        private val ENTITY_GROUP_LISTENER_METHODS = EntityGroupListener::class.java.declaredMethods



        fun implementsProcess(type: Class<out MasterSystem>) =
            type.hasMethod(PROCESS_METHOD, MASTER_SYSTEM_CLASS) ||
            type.hasMethod(PROCESS_SYSTEM_METHOD, MASTER_SYSTEM_CLASS)

        fun hasEntityGroupListener(type: Class<out MasterSystem>) =
            type.hasAnyMethod(ENTITY_GROUP_LISTENER_METHODS, MASTER_SYSTEM_CLASS)

        fun hasEntitySubscriptionListener(type: Class<out MasterSystem>) =
            type.hasAnyMethod(ENTITY_SUBSCRIPTION_LISTENER_METHODS, MASTER_SYSTEM_CLASS)

        fun hasEntityListener(type: Class<out MasterSystem>) =
            hasEntityGroupListener(type) || hasEntitySubscriptionListener(type)
    }
}