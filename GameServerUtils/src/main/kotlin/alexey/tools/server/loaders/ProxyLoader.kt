package alexey.tools.server.loaders

import alexey.tools.common.loaders.IndexGroup
import com.badlogic.gdx.utils.ObjectIntMap
import java.util.function.Function

class ProxyLoader <T> (private val pathsToIds: ObjectIntMap<String>,
                       private val group: IndexGroup<T>): Function<String, T?> {

    override fun apply(path: String): T? {
        val setId = pathsToIds.get(path, -1)
        return if (setId == -1) null else group.readObject(setId)
    }
}