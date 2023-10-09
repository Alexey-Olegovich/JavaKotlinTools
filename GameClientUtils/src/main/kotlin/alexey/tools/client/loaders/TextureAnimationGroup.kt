package alexey.tools.client.loaders

import alexey.tools.client.images.FramesConfig
import alexey.tools.client.images.TextureFrames
import alexey.tools.client.images.TextureRegions
import alexey.tools.common.loaders.PathGroup
import alexey.tools.common.loaders.BaseGroup
import alexey.tools.common.misc.PathUtils
import alexey.tools.server.loaders.JsonIO

class TextureAnimationGroup(private val jsonIO: JsonIO,
                            private val textureRegions: PathGroup<TextureRegions>): BaseGroup<TextureFrames>() {

    override fun readObject(path: String): TextureFrames? {
        val normalizedPath = PathUtils.normalizePath(path)

        var result = content[normalizedPath]
        if (result != null) return result
        val configPath = "$normalizedPath.json"
        result = content[configPath]
        if (result != null) {
            content[normalizedPath] = result
            return result
        }

        val regions = textureRegions.readObject(normalizedPath) ?: return null

        var config = try {
            jsonIO.readObject(normalizedPath, FramesConfig::class.java)
        } catch (_: Throwable) { null }
        if (config == null) {
            config = jsonIO.readObject(configPath, FramesConfig::class.java)
            result = if (config == null)
                TextureFrames(regions.data) else
                TextureFrames(regions.data, config)
            content[configPath] = result
        } else result = TextureFrames(regions.data, config)

        content[normalizedPath] = result
        return result
    }
}