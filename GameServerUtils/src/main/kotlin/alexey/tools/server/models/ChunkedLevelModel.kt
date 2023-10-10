package alexey.tools.server.models

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.context.ImmutableVariables
import alexey.tools.common.math.ImmutableIntVector2
import alexey.tools.common.math.toGrid

class ChunkedLevelModel(val chunkSize: Float = -1F,
                        var properties: ImmutableVariables = ImmutableVariables.DEFAULT) {

    private val data = HashMap<ImmutableIntVector2, ObjectList<EntityModel>>()



    fun add(entity: EntityModel) {
        data.getOrPut(toGrid(entity.x, entity.y, chunkSize)) { ObjectList() }.add(entity)
    }

    fun add(level: LevelModel) {
        level.layers.forEach { layer -> layer.entities.forEach { add(it) } }
    }

    fun get(position: ImmutableIntVector2): List<EntityModel> = data[position] ?: emptyList()

    fun asMap(): Map<ImmutableIntVector2, List<EntityModel>> = data

    fun clear() {
        data.clear()
    }



    companion object {
        val DEFAULT = ChunkedLevelModel()
    }
}