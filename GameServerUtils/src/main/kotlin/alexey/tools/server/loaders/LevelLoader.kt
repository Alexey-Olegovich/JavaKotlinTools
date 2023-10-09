package alexey.tools.server.loaders

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.loaders.PathGroup
import alexey.tools.common.misc.PathUtils
import alexey.tools.server.models.*
import java.util.function.Function

class LevelLoader(private val worldScale: Float,
                  private val tiledMaps: PathGroup<TiledMap>,
                  private val entitySets: PathGroup<EntitySet>): Function<String, LevelModel?> {

    override fun apply(path: String): LevelModel? {
        val tiledMap = tiledMaps.readObject(path) ?: return null
        val blankEntities = ObjectList<EntityModel>()
        tiledMap.tileSets.forEach {
            val entitySetPath = PathUtils.concatenatePaths(path, it.source)
            blankEntities.addAll((entitySets.readObject(entitySetPath)
                ?: throw NullPointerException("Missed entity set ($entitySetPath)!")).entities)
        }
        return tiledMap.toLevel(blankEntities, worldScale)
    }
}