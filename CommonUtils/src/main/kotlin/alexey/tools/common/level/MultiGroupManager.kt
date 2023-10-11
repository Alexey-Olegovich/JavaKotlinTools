package alexey.tools.common.level

import alexey.tools.common.collections.*
import alexey.tools.common.misc.Injector
import java.util.function.Function

class MultiGroupManager: Injector {

    private val groupsByIds = ObjectStorage<DefaultEntityGroup>()
    private val groupsByEntities = ObjectStorage<ObjectList<DefaultEntityGroup>>()

    private val idsByNames = HashMap<String, Int>()

    private var nextId = 0
    private val computeFunction = Function<String, Int> { nextId++ }



    fun putEntity(name: String, entityId: Int): Boolean =
        obtainGroup(name).put(entityId)

    fun putEntity(groupId: Int, entityId: Int): Boolean =
        getGroup(groupId).put(entityId)

    fun obtainGroup(name: String): EntityGroup {
        val id = idsByNames.computeIfAbsent(name, computeFunction)
        return groupsByIds.getOrExtendSet(id) { DefaultEntityGroup(name, id) }
    }

    fun obtainEntityGroups(entityId: Int): List<EntityGroup> =
        groupsByEntities.getOrExtendSet(entityId) { ObjectList(2) }

    fun getGroup(name: String): EntityGroup =
        groupsByIds[idsByNames[name] ?: throw NullPointerException()]

    fun getGroup(groupId: Int): EntityGroup =
        groupsByIds[groupId] ?: throw NullPointerException()

    fun getGroupOrNull(groupId: Int): EntityGroup? =
        groupsByIds.getOrNull(groupId)

    fun getEntityGroups(entityId: Int): List<EntityGroup> =
        groupsByEntities.getOrNull(entityId) ?: emptyList()

    fun getGroupOrNull(name: String): EntityGroup? {
        return groupsByIds[idsByNames[name] ?: return null]
    }

    fun clearGroup(groupId: Int) {
        groupsByIds.getOrNull(groupId)?.clear()
    }

    fun clearGroup(name: String) {
        groupsByIds[idsByNames[name] ?: return].clear()
    }

    fun removeEntity(entityId: Int): Boolean {
        val list = groupsByEntities.getOrNull(entityId) ?: return false
        if (list.isEmpty()) return false
        do list.removeLast().justClear(entityId) while (list.isNotEmpty)
        return true
    }



    override fun inject(target: Any) {
        var type = target.javaClass
        val globalName = type.getDeclaredAnnotation(Group::class.java)
        val globalGroup = if (globalName == null) EntityGroup.DEFAULT else obtainGroup(globalName.value)
        while (type !== Any::class.java) {
            for (field in type.declaredFields) {
                if (field.type !== EntityGroup::class.java) continue
                val group = field.getDeclaredAnnotation(Group::class.java)
                val entityGroup = if (group == null) globalGroup else obtainGroup(group.value)
                field.isAccessible = true
                field.set(target, entityGroup)
            }
            type = type.superclass
        }
    }



    private inner class DefaultEntityGroup(override val name: String,
                                           override val id: Int) : CachedIntSet(), EntityGroup {

        private val listeners = ObjectCollection<EntityGroupListener>(2)



        fun justClear(entityId: Int) {
            clear(entityId)
            notifyRemove(entityId)
        }



        override fun put(entityId: Int): Boolean {
            if (!super<CachedIntSet>.put(entityId)) return false
            putAndNotify(entityId)
            return true
        }

        override fun remove(entityId: Int): Boolean {
            if (!super<CachedIntSet>.remove(entityId)) return false
            removeAndNotify(entityId)
            return true
        }

        override fun clear() {
            forEachInt { removeAndNotify(it) }
            super<CachedIntSet>.clear()
        }

        override fun addListener(listener: EntityGroupListener) {
            listeners.add(listener)
        }

        override fun removeListener(listener: EntityGroupListener): Boolean {
            return listeners.removeReference(listener)
        }



        private fun removeAndNotify(entityId: Int) {
            groupsByEntities[entityId].removeReference(this)
            notifyRemove(entityId)
        }

        private fun putAndNotify(entityId: Int) {
            groupsByEntities.getOrExtendSet(entityId) { ObjectList(2) }.add(this)
            notifyPut(entityId)
        }

        private fun notifyPut(entityId: Int) {
            listeners.forEach { it.onInsert(this, entityId) }
        }

        private fun notifyRemove(entityId: Int) {
            listeners.forEach { it.onRemove(this, entityId) }
        }
    }
}