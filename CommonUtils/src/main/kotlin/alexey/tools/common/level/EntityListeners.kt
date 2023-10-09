package alexey.tools.common.level

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.collections.ObjectStorage
import alexey.tools.common.collections.getOrExtendSet

open class EntityListeners <T> {

    private val listeners = ObjectStorage<ObjectCollection<T>>()



    fun addListener(entityId: Int, listener: T) {
        listeners.getOrExtendSet(entityId) { ObjectCollection(2) }.add(listener)
    }

    fun removeListener(entityId: Int, listener: T) {
        listeners.getOrNull(entityId)?.removeReference(listener)
    }

    fun clearListeners(entityId: Int) {
        listeners.getOrNull(entityId)?.clear()
    }

    fun getListeners(entityId: Int): Collection<T>? {
        return listeners.getOrNull(entityId)
    }

    fun obtainListeners(entityId: Int): Collection<T> {
        return listeners.getOrNull(entityId) ?: emptyList()
    }

    open fun clearListeners() {
        listeners.clear()
    }
}