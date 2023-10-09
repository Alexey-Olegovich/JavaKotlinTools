package alexey.tools.common.level

import alexey.tools.common.collections.ImmutableIntSet

interface EntityGroup: ImmutableIntSet {

    val name: String
    val id: Int

    fun put(entityId: Int) = false
    fun remove(entityId: Int) = false
    fun addListener(listener: EntityGroupListener) {}
    fun removeListener(listener: EntityGroupListener) = false
    fun clear() {}

    companion object {
        val DEFAULT: EntityGroup = object : EntityGroup {
            override val name: String get() = ""
            override val id: Int get() = -1
        }
    }
}