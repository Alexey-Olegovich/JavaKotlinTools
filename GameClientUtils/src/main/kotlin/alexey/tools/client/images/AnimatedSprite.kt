package alexey.tools.client.images

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

class AnimatedSprite(val textureAnimation: TextureAnimation): Sprite() {

    constructor(animation: Animation<TextureRegion>): this(TextureAnimation(animation))

    private var index = -1



    fun render(batch: Batch, deltaTime: Float) {
        val i = textureAnimation.getIndex(deltaTime)
        if (index != i) {
            index = i
            setRegion(textureAnimation.getByIndex(i))
        }
        super.draw(batch)
    }

    override fun draw(batch: Batch) = render(batch, Gdx.graphics.deltaTime)
}