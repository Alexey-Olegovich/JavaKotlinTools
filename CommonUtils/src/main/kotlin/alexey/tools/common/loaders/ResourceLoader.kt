package alexey.tools.common.loaders

import alexey.tools.common.mods.ModLoader
import alexey.tools.common.resources.Resource
import java.util.function.Function

abstract class ResourceLoader <T> (protected val modLoader: ModLoader): Function<String, T?> {

    override fun apply(path: String): T? {
        return apply(modLoader.findResource(path) ?: return null)
    }

    abstract fun apply(resource: Resource): T
}