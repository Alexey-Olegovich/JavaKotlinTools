package alexey.tools.client.loaders

import alexey.tools.client.images.readTexture
import alexey.tools.common.loaders.ResourceLoader
import alexey.tools.common.mods.ModLoader
import alexey.tools.common.resources.Resource
import alexey.tools.server.loaders.DisposableGroup
import com.badlogic.gdx.graphics.Texture

class TextureLoader(modLoader: ModLoader): ResourceLoader<Texture>(modLoader) {
    override fun apply(resource: Resource): Texture = resource.readTexture()

    companion object {
        fun newGroup(modLoader: ModLoader) = DisposableGroup(TextureLoader(modLoader))
    }
}