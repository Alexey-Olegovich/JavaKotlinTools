package alexey.tools.server.loaders

import alexey.tools.common.loaders.PathGroup
import alexey.tools.server.models.EntitySet
import alexey.tools.server.models.TiledSet
import alexey.tools.server.models.toBlankEntities
import com.badlogic.gdx.utils.ObjectIntMap
import java.util.function.Function

class MappedEntitySetLoader(private val worldScale: Float,
                            private val idsByPaths: ObjectIntMap<String>,
                            private val tiledSets: PathGroup<TiledSet>): Function<String, EntitySet?> {

    override fun apply(path: String): EntitySet? {
        val tiledSet = tiledSets.readObject(path) ?: return null
        val id = idsByPaths.get(path, -1)
        return EntitySet(tiledSet.toBlankEntities(worldScale,  id.toLong().shl(32)), id)
    }
}