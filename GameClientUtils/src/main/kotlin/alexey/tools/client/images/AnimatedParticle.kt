package alexey.tools.client.images

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

class AnimatedParticle(animation: Animation<TextureRegion>,
                       val sprite: Sprite = Sprite()): TextureAnimation(animation) {

    private var index = -1



    fun draw(batch: Batch, deltaTime: Float) {
        val i = getIndex(deltaTime)
        if (index != i) {
            index = i
            val flipX = sprite.isFlipX
            val flipY = sprite.isFlipY
            sprite.setRegion(getByIndex(i))
            sprite.flip(flipX, flipY)
        }
        sprite.draw(batch)
    }
}