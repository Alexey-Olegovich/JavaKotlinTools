package alexey.tools.client.images

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureFrames(frames: Array<TextureRegion>,
                    frameDuration: Float = 0.1F,
                    playMode: PlayMode = PlayMode.LOOP): Animation<TextureRegion>(frameDuration, *frames) {

    constructor(frames: Array<TextureRegion>,
                config: FramesConfig): this(frames, config.frameDuration, config.playMode)

    constructor(frames: Texture, config: FramesConfig):
            this(frames.toArray(config.rows, config.cols), config)



    init { this.playMode = playMode }



    companion object {
        val EMPTY = TextureFrames(emptyArray(), 0.1F, PlayMode.LOOP)
    }
}