package alexey.tools.common.resources

import alexey.tools.common.mods.ModLoader

class ModLoaderResource(private val modLoader: ModLoader): Resource {
    override fun getResource(relativePath: String): Resource = modLoader.findResource(relativePath) ?: Resource.NULL
    override fun getResourceType(): String = "loader-mod"
    override fun getPath(): String = modLoader.toString()
}