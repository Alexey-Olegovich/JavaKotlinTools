package alexey.tools.client.images

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.ObjectStorage
import alexey.tools.common.collections.getOrExtendSet
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

class ParticlePlayer {

    private val animatedParticles = ObjectStorage<ObjectCollection<AnimatedParticle>>()



    fun draw(batch: Batch, deltaTime: Float, layer: Int) {
        val i = animatedParticles.getOrNull(layer)?.iterator() ?: return
        while (i.hasNext()) {
            val task = i.next()
            task.draw(batch, deltaTime)
            if (task.isFinished()) i.remove()
        }
    }

    fun add(frames: Animation<TextureRegion>,
            layer: Int, x: Float, y: Float, width: Float, height: Float): Sprite {
        val sprite = add(frames, layer)
        sprite.setSize(width, height)
        sprite.setPosition(x - width / 2F, y - height / 2F)
        return sprite
    }

    fun add(frames: Animation<TextureRegion>, layer: Int, sprite: Sprite): Sprite {
        animatedParticles.getOrExtendSet(layer) { ObjectList() }.add(AnimatedParticle(frames, Sprite(sprite)))
        return sprite
    }

    fun add(frames: Animation<TextureRegion>, layer: Int): Sprite {
        val sprite = Sprite()
        animatedParticles.getOrExtendSet(layer) { ObjectList() }.add(AnimatedParticle(frames, sprite))
        return sprite
    }

    fun size() = animatedParticles.size
}