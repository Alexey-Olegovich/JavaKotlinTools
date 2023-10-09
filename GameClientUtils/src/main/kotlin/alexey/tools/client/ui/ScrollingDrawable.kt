package alexey.tools.client.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class ScrollingDrawable(texture: Texture, private val speed: Vector2 = Vector2.Zero): TextureRegionDrawable(texture) {

    init {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    }



    fun scroll() {
        val delta = Gdx.graphics.deltaTime
        region.scroll(speed.x * delta, speed.y * delta)
    }

    override fun draw(batch: Batch?, x: Float, y: Float, width: Float, height: Float) {
        scroll()
        super.draw(batch, x, y, width, height)
    }

    override fun draw(
        batch: Batch?,
        x: Float,
        y: Float,
        originX: Float,
        originY: Float,
        width: Float,
        height: Float,
        scaleX: Float,
        scaleY: Float,
        rotation: Float
    ) {
        scroll()
        super.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }

}