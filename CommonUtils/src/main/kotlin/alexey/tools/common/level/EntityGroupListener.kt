package alexey.tools.common.level

interface EntityGroupListener {
    fun onInsert(group: EntityGroup, entityId: Int) {}
    fun onRemove(group: EntityGroup, entityId: Int) {}

    companion object {
        val DEFAULT = object : EntityGroupListener {}
    }
}