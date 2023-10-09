package alexey.tools.server.level

import alexey.tools.common.collections.IntCollection
import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.level.Chunk
import alexey.tools.common.level.ChunkManager
import alexey.tools.common.level.EntityListeners
import alexey.tools.common.level.StateManager
import alexey.tools.common.math.toGrid
import com.badlogic.gdx.math.Vector2

class AdvancedChunkManager(val chunkSize: Float = 16F,
                           visibleRadius: Int = 2): ChunkManager(visibleRadius) {

    private val stateManager = StateManager()
    private val listeners = ObjectCollection<Listener>(4)



    init { super.putListener(AdvancedListener()) }



    override fun putListener(listener: Listener) { listeners.add(listener) }

    fun removeListener(listener: Listener) = listeners.removeReference(listener)

    override fun clearListener() { listeners.clear() }

    fun getEntityListeners(): EntityListeners<StateManager.Listener> = stateManager

    fun clearListeners() { clearListener(); stateManager.clearListeners() }



    fun toChunkCoordinates(x: Float, y: Float) = toGrid(x, y, chunkSize, chunkSize)

    fun toChunkX(x: Float) = toGrid(x, chunkSize)

    fun toChunkY(y: Float) = toGrid(y, chunkSize)



    fun toChunkCoordinates(position: Vector2) = toChunkCoordinates(position.x, position.y)

    fun getChunk(position: Vector2): Chunk? = getChunk(toChunkCoordinates(position))

    fun obtainChunk(position: Vector2): Chunk = obtainChunk(toChunkCoordinates(position))



    private inner class AdvancedListener: Listener {
        override fun onCreate(chunk: Chunk) {
            listeners.forEach { it.onCreate(chunk) }
            stateManager.onCreate(chunk)
        }

        override fun onDisable(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
            listeners.forEach { it.onDisable(entities, activators, chunk, last) }
            stateManager.onDisable(entities, activators, chunk, last)
        }

        override fun onEnable(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
            listeners.forEach { it.onEnable(entities, activators, chunk, first) }
            stateManager.onEnable(entities, activators, chunk, first)
        }

        override fun onRemove(chunk: Chunk) {
            listeners.forEach { it.onRemove(chunk) }
            stateManager.onRemove(chunk)
        }

        override fun onHide(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
            listeners.forEach { it.onHide(entities, activators, chunk, last) }
            stateManager.onHide(entities, activators, chunk, last)
        }

        override fun onShow(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
            listeners.forEach { it.onShow(entities, activators, chunk, first) }
            stateManager.onShow(entities, activators, chunk, first)
        }
    }
}