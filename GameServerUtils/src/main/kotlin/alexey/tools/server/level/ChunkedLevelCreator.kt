package alexey.tools.server.level

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.math.ImmutableIntMatrix2
import alexey.tools.server.models.ChunkedLevelModel
import alexey.tools.server.models.EntityModel
import alexey.tools.server.models.LevelModel

class ChunkedLevelCreator {

    val structures = ObjectList<LevelModel>()
    var floor = EntityModel.DEFAULT
    var wall = EntityModel.DEFAULT
    var tileSize = 1F
    var chunkSize = 16F
    var wallId = 1
    var voidId = 0
    var globalOffsetX = 0F
    var globalOffsetY = 0F



    fun create(data: ImmutableIntMatrix2): ChunkedLevelModel {
        val result = ChunkedLevelModel(chunkSize)
        for (x in 0 ..< data.width) for (y in 0 ..< data.height) {
            val tileId = data.get(x, y)
            if (tileId == voidId) continue
            val offsetX = x * tileSize + globalOffsetX
            val offsetY = y * tileSize + globalOffsetY
            result.add(if (tileId == wallId) wall.with(offsetX, offsetY, 1) else floor.with(offsetX, offsetY, 0))
            if (tileId < 3) continue
            val layers = structures[tileId - 3].layers
            var i = 0
            while (i < layers.size) layers[i++].entities.forEach {
                result.add(it.with(it.x + offsetX, it.y + offsetY, i))
            }
        }
        return result
    }
}