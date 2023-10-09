package alexey.tools.common.level

import alexey.tools.common.collections.ImmutableIntSet
import alexey.tools.common.math.ImmutableIntVector2

interface Chunk {
    fun add(entityId: Int, observer: Boolean) = if (observer) addAsObserver(entityId) else addAsEntity(entityId)
    fun addAsObserver(entityId: Int): Boolean = false
    fun addAsEntity(entityId: Int): Boolean = false
    fun remove(): Boolean = false

    fun getEntities(): ImmutableIntSet = ImmutableIntSet.EMPTY
    fun getObservers(): ImmutableIntSet = ImmutableIntSet.EMPTY
    fun getUpdaters(): ImmutableIntSet = ImmutableIntSet.EMPTY
    fun getPosition(): ImmutableIntVector2 = ImmutableIntVector2.ZERO

    fun isValid(): Boolean = false
    fun isEnabled(): Boolean = false
    fun isVisible(): Boolean = false
    fun isVisibleFor(entityId: Int): Boolean = false
    fun isEnabledFor(entityId: Int): Boolean = false
    fun contains(entityId: Int): Boolean = false

    companion object {
        val NULL = object : Chunk { }
    }
}