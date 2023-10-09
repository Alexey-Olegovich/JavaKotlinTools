package alexey.tools.server.level

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.context.ImmutableVariables
import alexey.tools.common.math.ImmutableIntVector2
import alexey.tools.common.math.toGrid
import alexey.tools.server.models.ChunkedLevelModel
import alexey.tools.server.models.EntityModel
import alexey.tools.server.models.LevelModel

class ChunkedLevel(override val chunkSize: Float,
                   override var properties: ImmutableVariables = ImmutableVariables.DEFAULT): ChunkedLevelModel {

    private val data = HashMap<ImmutableIntVector2, ObjectList<EntityModel>>()



    fun add(entity: EntityModel) {
        data.getOrPut(toGrid(entity.x, entity.y, chunkSize)) { ObjectList() }.add(entity)
    }

    fun add(level: LevelModel) {
        level.layers.forEach { layer -> layer.entities.forEach { add(it) } }
    }

    override fun get(position: ImmutableIntVector2): List<EntityModel> = data[position] ?: emptyList()

    override fun asMap(): Map<ImmutableIntVector2, List<EntityModel>> = data

    fun clear() {
        data.clear()
    }
}