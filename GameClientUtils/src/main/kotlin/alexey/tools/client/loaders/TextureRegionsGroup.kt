package alexey.tools.client.loaders

import alexey.tools.client.images.RegionsConfig
import alexey.tools.client.images.TextureRegions
import alexey.tools.client.images.toArray
import alexey.tools.common.loaders.PathGroup
import alexey.tools.common.loaders.BaseGroup
import alexey.tools.common.misc.PathUtils
import alexey.tools.common.misc.removeExtension
import alexey.tools.server.loaders.JsonIO
import alexey.tools.server.models.TiledSet
import com.badlogic.gdx.graphics.Texture

class TextureRegionsGroup(private val tiledSets: PathGroup<TiledSet>,
                          private val textures: PathGroup<Texture>,
                          private val jsonIO: JsonIO): BaseGroup<TextureRegions>() {

    override fun readObject(path: String): TextureRegions? {
        val normalizedPath = PathUtils.normalizePath(path)
        var textureRegions = content[normalizedPath]
        if (textureRegions != null) return textureRegions
        textureRegions =
            readWithTiledSet(normalizedPath) ?:
            readWithConfig(normalizedPath) ?:
            readWithTexture(normalizedPath) ?: return null
        content[normalizedPath] = textureRegions
        return textureRegions
    }



    private fun readWithTiledSet(tiledSetPath: String): TextureRegions? {
        try {
            val tiledSet = tiledSets.readObject(tiledSetPath) ?: return null
            val texturePath = PathUtils.concatenatePaths(tiledSetPath, tiledSet.image)
            val configPath = PathUtils.normalizePath("$texturePath.json")
            var textureRegions = content[configPath]
            if (textureRegions != null) return textureRegions
            textureRegions = TextureRegions((textures.readObject(texturePath) ?: return null)
                .toArray(tiledSet.tileWidth, tiledSet.tileHeight, tiledSet.tileCount))
            content[configPath] = textureRegions
            return textureRegions
        } catch (_: Throwable) {
            return null
        }
    }

    private fun readWithConfig(configPath: String): TextureRegions? {
        try {
            val config = jsonIO.readObject(configPath, RegionsConfig::class.java) ?: return null
            return TextureRegions(textures.readObject(config.source.ifEmpty { configPath.removeExtension() })
                ?: return null, config)
        } catch (_: Throwable) {
            return null
        }
    }

    private fun readWithTexture(texturePath: String): TextureRegions? {
        val texture = textures.readObject(texturePath) ?: return null
        val config = jsonIO.readObject("$texturePath.json", RegionsConfig::class.java)
        val textureRegions = if (config == null) TextureRegions(texture) else TextureRegions(texture, config)
        content[texturePath] = textureRegions
        return textureRegions
    }
}