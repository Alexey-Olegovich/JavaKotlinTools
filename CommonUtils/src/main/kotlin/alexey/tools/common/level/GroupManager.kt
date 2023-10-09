package alexey.tools.common.level

import alexey.tools.common.collections.*
import alexey.tools.common.misc.Injector
import java.util.function.Function

class GroupManager: Injector {

    private val groupsByIds = ObjectStorage<DefaultEntityGroup>()
    private val groupsByEntities = ObjectStorage<DefaultEntityGroup>()

    private val idsByNames = HashMap<String, Int>()

    private var nextId = 0
    private val computeFunction = Function<String, Int> { nextId++ }



    fun putEntity(name: String, entityId: Int): Boolean =
        obtainGroup(name).put(entityId)

    fun obtainGroup(name: String): EntityGroup {
        val id = idsByNames.computeIfAbsent(name, computeFunction)
        return groupsByIds.getOrExtendSet(id) { DefaultEntityGroup(name, id) }
    }

    fun getGroup(name: String): EntityGroup =
        groupsByIds[idsByNames[name] ?: throw NullPointerException()]

    fun getGroup(groupId: Int): EntityGroup =
        groupsByIds[groupId] ?: throw NullPointerException()

    fun getGroupOrNull(groupId: Int): EntityGroup? =
        groupsByIds.getOrNull(groupId)

    fun getGroupOrNull(name: String): EntityGroup? {
        return groupsByIds[idsByNames[name] ?: return null]
    }

    fun getEntityGroup(id: Int): EntityGroup =
        groupsByEntities[id] ?: throw NullPointerException()

    fun getEntityGroupOrNull(id: Int): EntityGroup? =
        groupsByEntities.getOrNull(id)

    fun clearGroup(groupId: Int) {
        groupsByIds.getOrNull(groupId)?.clear()
    }

    fun clearGroup(name: String) {
        groupsByIds[idsByNames[name] ?: return].clear()
    }

    fun removeEntity(entityId: Int): Boolean = groupsByEntities.getOrNull(entityId)?.justClear(entityId) ?: false



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



        fun justClear(entityId: Int): Boolean {
            clear(entityId)
            groupsByEntities.justSetNull(entityId)
            notifyRemove(entityId)
            return true
        }



        override fun put(entityId: Int): Boolean {
            if (!groupsByEntities.isNull(entityId)) return false
            add(entityId)
            groupsByEntities.extendSet(entityId, this)
            notifyPut(entityId)
            return true
        }

        override fun remove(entityId: Int): Boolean =
            if (groupsByEntities.equalReference(entityId, this)) justClear(entityId) else false

        override fun addListener(listener: EntityGroupListener) {
            listeners.add(listener)
        }

        override fun removeListener(listener: EntityGroupListener): Boolean {
            return listeners.removeReference(listener)
        }

        override fun clear() {
            forEachInt {
                groupsByEntities.justSet(it, null)
                notifyRemove(it)
            }
            super<CachedIntSet>.clear()
        }



        private fun notifyPut(entityId: Int) {
            listeners.forEach { it.onInsert(this, entityId) }
        }

        private fun notifyRemove(entityId: Int) {
            listeners.forEach { it.onRemove(this, entityId) }
        }
    }
}