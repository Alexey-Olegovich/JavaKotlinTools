package alexey.tools.client.images

import alexey.tools.common.collections.RotatingArray
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

open class TextureAnimation(val animation: Animation<TextureRegion>,
                            var sateTime: Float = 0F): RotatingArray<TextureRegion> {

    override val data: Array<TextureRegion> get() = animation.keyFrames



    fun getByState(delta: Float): TextureRegion {
        sateTime += delta
        return animation.getKeyFrame(sateTime)
    }

    fun getByState(): TextureRegion {
        return animation.getKeyFrame(sateTime)
    }

    fun getIndex(delta: Float): Int {
        sateTime += delta
        return animation.getKeyFrameIndex(sateTime)
    }

    fun getIndex(): Int {
        return animation.getKeyFrameIndex(sateTime)
    }

    fun copy() = TextureAnimation(animation, sateTime)
    fun isFinished() = sateTime / animation.animationDuration >= 1F
}