package alexey.tools.client.images

import alexey.tools.server.misc.getOrPut
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ObjectMap

class ColoredTextures: Disposable {

    private val data = ObjectMap<Color, Texture>()



    fun obtain(color: Color): Texture =
        data.getOrPut(color) { newTexture(1, 1, color) }

    override fun dispose() {
        data.values().forEach { it.dispose() }
    }
}