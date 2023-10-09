package alexey.tools.common.level

open class EntityGroupListenerWrapper(val listener: EntityGroupListener): EntityGroupListener {

    override fun onInsert(group: EntityGroup, entityId: Int) {
        listener.onInsert(group, entityId)
    }

    override fun onRemove(group: EntityGroup, entityId: Int) {
        listener.onRemove(group, entityId)
    }
}