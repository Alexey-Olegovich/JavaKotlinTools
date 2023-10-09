package alexey.tools.common.level

import alexey.tools.common.collections.IntCollection
import alexey.tools.common.collections.forEachInt

class StateManager: EntityListeners<StateManager.Listener>(), ChunkManager.Listener {

    override fun onEnable(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        entities.forEachInt { entityId ->
            getListeners(entityId)?.forEach {
                it.onEnable(entityId, activators, chunk, first)
            }
        }
    }

    override fun onDisable(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        entities.forEachInt { entityId ->
            getListeners(entityId)?.forEach {
                it.onDisable(entityId, activators, chunk, last)
            }
        }
    }

    override fun onShow(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        entities.forEachInt { entityId ->
            getListeners(entityId)?.forEach {
                it.onShow(entityId, activators, chunk, first)
            }
        }
    }

    override fun onHide(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        entities.forEachInt { entityId ->
            getListeners(entityId)?.forEach {
                it.onHide(entityId, activators, chunk, last)
            }
        }
    }



    interface Listener {
        fun onEnable(entityId: Int, activators: IntCollection, chunk: Chunk, first: Boolean) {}
        fun onDisable(entityId: Int, activators: IntCollection, chunk: Chunk, last: Boolean) {}
        fun onShow(entityId: Int, activators: IntCollection, chunk: Chunk, first: Boolean) {}
        fun onHide(entityId: Int, activators: IntCollection, chunk: Chunk, last: Boolean) {}

        companion object {
            val DEFAULT = object : Listener {}
        }
    }
}