package alexey.tools.common.level

import alexey.tools.common.collections.*
import alexey.tools.common.math.ImmutableIntVector2
import alexey.tools.common.math.IntVector2
import alexey.tools.common.collections.forEachInt

open class ChunkManager(val visibleRadius: Int = 2) {

    private val chunks = HashMap<ImmutableIntVector2, MutableChunk>()
    private val entityToChunk = ObjectStorage<MutableChunk>()
    private val origins = ObjectStorage<ImmutableIntVector2>()
    private var listener = Listener.DEFAULT

    private val temp = IntList()
    private val tempSingleton = IntListSingleton(-1)



    open fun putListener(listener: Listener) { this.listener = listener }

    open fun clearListener() { listener = Listener.DEFAULT }

    fun getListener() = listener



    fun getChunk(x: Int, y: Int): Chunk? = getChunk(IntVector2(x, y))

    fun getChunk(position: ImmutableIntVector2): Chunk? = chunks[position]

    fun getChunk(entityId: Int): Chunk? = entityToChunk.getOrNull(entityId)



    fun obtainChunk(x: Int, y: Int): Chunk = obtainChunk(IntVector2(x, y))

    fun obtainChunk(position: ImmutableIntVector2): Chunk = chunks[position] ?: createChunk0(position)

    fun obtainChunk(entityId: Int): Chunk = entityToChunk.getOrNull(entityId) ?: Chunk.NULL



    fun removeChunk(x: Int, y: Int) = removeChunk(IntVector2(x, y))

    fun removeChunk(position: ImmutableIntVector2): Chunk? {
        return (chunks[position] ?: return null).apply { remove() }
    }



    fun moveTo(entityId: Int, to: ImmutableIntVector2): Chunk {
        val chunk = entityToChunk.getOrNull(entityId) ?: return Chunk.NULL
        if (chunk.getPosition() == to) return chunk
        return chunk.moveTo(entityId, to)
    }

    fun moveTo(entityId: Int, toX: Int, toY: Int): Chunk =
        moveTo(entityId, IntVector2(toX, toY))

    fun updateOrigin(entityId: Int): Boolean {
        val position = (entityToChunk.getOrNull(entityId) ?: return false).getPosition()

        val origin = origins.getOrNull(entityId)
        if (origin == null || origin === position) return false

        val fromBounds = ViewRectangle(origin  , visibleRadius    )
        val toBounds   = ViewRectangle(position, visibleRadius    )
        val fromBorder = ViewRectangle(origin  , visibleRadius + 1)
        val toBorder   = ViewRectangle(position, visibleRadius + 1)

        fromBorder.forEachBorder(toBorder  ) { x, y -> disableChunkFor(x, y, entityId) }
        fromBounds.forEach      (toBounds  ) { x, y -> disableChunkFor(x, y, entityId) }
        toBounds  .forEach      (fromBounds) { x, y -> showChunkFor   (x, y, entityId) }
        toBorder  .forEachBorder(fromBorder) { x, y -> enableChunkFor (x, y, entityId) }

        origins.justSet(entityId, position)
        return true
    }

    fun remove(entityId: Int): Boolean =
        entityToChunk.getOrNull(entityId)?.remove(entityId) ?: false



    private fun createChunk0(position: ImmutableIntVector2): MutableChunk {
        val chunk = MutableChunk(position)
        chunks[position] = chunk
        listener.onCreate(chunk)
        return chunk
    }

    private fun obtainChunk0(position: ImmutableIntVector2): MutableChunk =
        chunks[position] ?: createChunk0(position)



    private fun enableChunkFor(x: Int, y: Int, entityId: Int) =
        enableChunkFor(IntVector2(x, y), entityId)

    private fun enableChunkFor(position: ImmutableIntVector2, entityId: Int): MutableChunk =
        obtainChunk0(position).apply { enableFor(entityId) }



    private fun disableChunkFor(x: Int, y: Int, entityId: Int) =
        disableChunkFor(IntVector2(x, y), entityId)

    private fun disableChunkFor(position: ImmutableIntVector2, entityId: Int): MutableChunk? =
        chunks[position]?.apply { disableFor(entityId) }



    private fun showChunkFor(x: Int, y: Int, entityId: Int) =
        showChunkFor(IntVector2(x, y), entityId)

    private fun showChunkFor(position: ImmutableIntVector2, entityId: Int): MutableChunk =
        obtainChunk0(position).apply { showFor(entityId) }



    private fun hideChunkFor(x: Int, y: Int, entityId: Int) =
        hideChunkFor(IntVector2(x, y), entityId)

    private fun hideChunkFor(position: ImmutableIntVector2, entityId: Int): MutableChunk? =
        chunks[position]?.apply { hideFor(entityId) }



    private fun use(entityId: Int): ImmutableIntList = tempSingleton.apply { value = entityId }



    private inner class MutableChunk(private val position: ImmutableIntVector2,
                                     private val updaters: CachedIntSet = CachedIntSet(),
                                     private val observers: CachedIntSet = CachedIntSet()): Chunk {

        private val entities = CachedIntSet()
        private var isValid = true



        override fun addAsObserver(entityId: Int): Boolean {
            if (!canAdd(entityId)) return false
            origins.extendSet(entityId, position)
            showFor(entityId)
            ViewRectangle(position, visibleRadius).forEach { x, y ->
                if (!position.equals(x, y)) showChunkFor(x, y, entityId)
            }
            ViewRectangle(position, visibleRadius + 1).forEachBorder { x, y ->
                enableChunkFor(x, y, entityId)
            }
            insertEntity(entityId)
            return true
        }

        override fun addAsEntity(entityId: Int): Boolean {
            if (!canAdd(entityId)) return false
            insertEntity(entityId)
            return true
        }

        override fun getObservers(): ImmutableIntSet = observers

        override fun getUpdaters(): ImmutableIntSet = updaters

        override fun getEntities(): ImmutableIntSet = entities

        override fun getPosition(): ImmutableIntVector2 = position

        override fun isEnabled(): Boolean = isValid && updaters.isNotEmpty

        override fun isVisible(): Boolean = isValid && observers.isNotEmpty

        override fun isValid(): Boolean = isValid

        override fun isEnabledFor(entityId: Int): Boolean = isValid && updaters.contains(entityId)

        override fun isVisibleFor(entityId: Int): Boolean = isValid && observers.contains(entityId)

        override fun contains(entityId: Int): Boolean =
            isValid && entityToChunk.equalReference(entityId, this)

        override fun remove(): Boolean {
            if (!isValid || updaters.isNotEmpty) return false
            chunks.remove(position)
            isValid = false
            entities.forEachInt { entityToChunk.justSet(it, null) }
            listener.onRemove(this)
            return true
        }



        fun moveTo(entityId: Int, to: ImmutableIntVector2): Chunk {
            val toChunk = obtainChunk0(to)

            if (updaters.contains(entityId)) toChunk.showFor(entityId)

            entities.unsafeClear(entityId)
            toChunk.entities.add(entityId)
            entityToChunk.justSet(entityId, toChunk)

            temp.setClear(observers, toChunk.observers)
            if (!temp.isEmpty) hideEntityFor(entityId, temp, this, toChunk.observers.isEmpty)
            temp.setClear(updaters, toChunk.updaters)
            if (!temp.isEmpty) disableEntityFor(entityId, temp, this, toChunk.updaters.isEmpty)
            temp.setClear(toChunk.updaters, updaters)
            if (!temp.isEmpty) enableEntityFor(entityId, temp, toChunk, updaters.isEmpty)
            temp.setClear(toChunk.observers, observers)
            if (!temp.isEmpty) showEntityFor(entityId, temp, toChunk, observers.isEmpty)

            return toChunk
        }

        fun remove(entityId: Int): Boolean {
            if (updaters.contains(entityId)) {
                disableFor(entityId)
                ViewRectangle(origins.setNull(entityId), visibleRadius + 1).forEach { x, y ->
                    if (!position.equals(x, y)) disableChunkFor(x, y, entityId)
                }
            }

            entityToChunk.justSet(entityId, null)
            entities.unsafeClear(entityId)

            if (observers.isNotEmpty) hideEntityFor(entityId, observers, this, true)
            if (updaters.isNotEmpty) disableEntityFor(entityId, updaters, this, true)

            return true
        }

        fun showFor(entityId: Int) {
            when {
                entities.isEmpty -> {
                    updaters.add(entityId)
                    observers.add(entityId)
                }
                updaters.put(entityId) -> {
                    enableEntitiesFor(entityId)
                    observers.add(entityId)
                    showEntitiesFor(entityId)
                }
                observers.put(entityId) -> {
                    showEntitiesFor(entityId)
                }
            }
        }

        fun hideFor(entityId: Int) {
            when {
                entities.isEmpty -> observers.clear()
                observers.remove(entityId) -> hideEntitiesFor(entityId)
            }
        }

        fun enableFor(entityId: Int) {
            when {
                entities.isEmpty -> updaters.add(entityId)
                updaters.put(entityId) -> enableEntitiesFor(entityId)
            }
        }

        fun disableFor(entityId: Int) {
            when {
                entities.isEmpty -> {
                    updaters.clear(entityId)
                    observers.clear(entityId)
                }
                observers.remove(entityId) -> {
                    hideEntitiesFor(entityId)
                    updaters.unsafeClear(entityId)
                    disableEntitiesFor(entityId)
                }
                updaters.remove(entityId) -> {
                    disableEntitiesFor(entityId)
                }
            }
        }

        fun setEnabledFor(entityId: Int) {
            when {
                entities.isEmpty -> {
                    updaters.add(entityId)
                    observers.clear(entityId)
                }
                observers.remove(entityId) -> {
                    hideEntitiesFor(entityId)
                }
                updaters.put(entityId) -> {
                    enableEntitiesFor(entityId)
                }
            }
        }



        private fun enableEntitiesFor(entityId: Int) {
            listener.onEnable(entities, use(entityId), this, updaters.size() == 1)
        }

        private fun disableEntitiesFor(entityId: Int) {
            listener.onDisable(entities, use(entityId), this, updaters.isEmpty)
        }

        private fun showEntitiesFor(entityId: Int) {
            listener.onShow(entities, use(entityId), this, observers.size() == 1)
        }

        private fun hideEntitiesFor(entityId: Int) {
            listener.onHide(entities, use(entityId), this, observers.isEmpty)
        }

        private fun enableEntityFor(entityId: Int, activators: IntCollection, chunk: Chunk, first: Boolean) {
            listener.onEnable(use(entityId), activators, chunk, first)
        }

        private fun disableEntityFor(entityId: Int, activators: IntCollection, chunk: Chunk, first: Boolean) {
            listener.onDisable(use(entityId), activators, chunk, first)
        }

        private fun showEntityFor(entityId: Int, activators: IntCollection, chunk: Chunk, first: Boolean) {
            listener.onShow(use(entityId), activators, chunk, first)
        }

        private fun hideEntityFor(entityId: Int, activators: IntCollection, chunk: Chunk, first: Boolean) {
            listener.onHide(use(entityId), activators, chunk, first)
        }

        private fun insertEntity(entityId: Int) {
            entityToChunk.extendSet(entityId, this)
            entities.add(entityId)
            if (updaters.isNotEmpty) enableEntityFor(entityId, updaters, this, true)
            if (observers.isNotEmpty) showEntityFor(entityId, observers, this, true)
        }

        private fun canAdd(entityId: Int): Boolean =
            isValid && entityToChunk.isNull(entityId)
    }



    interface Listener {
        fun onCreate(chunk: Chunk) {}
        fun onRemove(chunk: Chunk) {}
        fun onEnable(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {}
        fun onDisable(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {}
        fun onShow(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {}
        fun onHide(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {}

        companion object {
            val DEFAULT = object : Listener {}
        }
    }
}