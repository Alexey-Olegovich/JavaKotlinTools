package alexey.tools.server.loaders

import alexey.tools.common.loaders.PathGroup
import alexey.tools.server.models.EntitySet
import alexey.tools.server.models.TiledSet
import alexey.tools.server.models.toBlankEntities
import java.util.function.IntFunction

class EntitySetIndexLoader(private val worldScale: Float,
                           private val idsToPaths: List<String>,
                           private val tiledSets: PathGroup<TiledSet>): IntFunction<EntitySet?> {

    override fun apply(value: Int): EntitySet? {
        val tiledSet = tiledSets.readObject(idsToPaths.getOrNull(value) ?: return null) ?: return null
        return EntitySet(tiledSet.toBlankEntities(worldScale,  value.toLong().shl(32)), value)
    }
}