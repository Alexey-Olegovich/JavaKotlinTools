package alexey.tools.server.world

import alexey.tools.common.collections.forEachInt
import alexey.tools.common.level.*
import alexey.tools.common.misc.hasAnyMethod
import alexey.tools.common.misc.hasMethod
import java.lang.reflect.Method

open class GroupMasterSystem: MasterSystem(), MasterEntityListener {

    var entityGroup = EntityGroup.DEFAULT
        protected set



    fun hasEntityGroupListener() = hasEntityGroupListener(javaClass)

    fun hasEntityListener() = hasEntityListener(javaClass)

    override fun implementsProcess() = implementsProcess(javaClass)

    override fun hasEntitySubscriptionListener() = hasEntitySubscriptionListener(javaClass)

    fun hasEntities() = hasEntityGroup() || hasEntitySubscription()

    fun hasEntityGroup() = entityGroup !== EntityGroup.DEFAULT

    fun addListener(listener: MasterEntityListener) {
        if (entityGroup === EntityGroup.DEFAULT)
            getEntitySubscription().addSubscriptionListener(listener) else
            entityGroup.addListener(listener)
    }

    override fun addSelf() {
        if (hasEntities() && hasEntitySubscriptionListener()) addListener(this)
    }



    override fun processSystem() {
        if (entityGroup === EntityGroup.DEFAULT)
            getEntitySubscription().entities.forEach { process(it) } else
            entityGroup.forEachInt { process(it) }
    }



    companion object {

        val MASTER_SYSTEM_CLASS = GroupMasterSystem::class.java
        val ENTITY_GROUP_LISTENER_METHODS: Array<Method> = EntityGroupListener::class.java.declaredMethods



        fun implementsProcess(type: Class<out GroupMasterSystem>) =
            type.hasMethod(PROCESS_METHOD, MASTER_SYSTEM_CLASS) ||
            type.hasMethod(PROCESS_SYSTEM_METHOD, MASTER_SYSTEM_CLASS)

        fun hasEntityGroupListener(type: Class<out GroupMasterSystem>) =
            type.hasAnyMethod(ENTITY_GROUP_LISTENER_METHODS, MASTER_SYSTEM_CLASS)

        fun hasEntitySubscriptionListener(type: Class<out GroupMasterSystem>) =
            type.hasAnyMethod(ENTITY_SUBSCRIPTION_LISTENER_METHODS, MASTER_SYSTEM_CLASS)

        fun hasEntityListener(type: Class<out GroupMasterSystem>) =
            hasEntityGroupListener(type) || hasEntitySubscriptionListener(type)
    }
}