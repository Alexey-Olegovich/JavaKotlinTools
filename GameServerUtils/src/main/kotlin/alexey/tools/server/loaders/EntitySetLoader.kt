package alexey.tools.server.loaders

import alexey.tools.common.loaders.PathGroup
import alexey.tools.server.models.EntitySet
import alexey.tools.server.models.toBlankEntities
import alexey.tools.server.models.TiledSet
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function

class EntitySetLoader(private val worldScale: Float,
                      private val tiledSets: PathGroup<TiledSet>): Function<String, EntitySet?> {

    private val nextId = AtomicInteger(0)



    override fun apply(path: String): EntitySet? {
        val tiledSet = tiledSets.readObject(path) ?: return null
        val id = nextId.getAndIncrement()
        return EntitySet(tiledSet.toBlankEntities(worldScale,  id.toLong().shl(32)), id)
    }
}