package alexey.tools.client.images

import alexey.tools.common.collections.MirroredRotatingArray
import alexey.tools.common.collections.DefaultRotatingArray
import alexey.tools.common.collections.RotatingArray
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureRegions(override val data: Array<TextureRegion>): RotatingArray<TextureRegion> {
    constructor(texture: Texture, cols: Int = 1, rows: Int = 1):
            this(texture.toArray(rows, cols))

    constructor(texture: Texture, config: RegionsConfig):
            this(texture, config.cols, config.rows)

    fun toMirrored() = MirroredRotatingArray(data)
    fun toDefault() = DefaultRotatingArray(data)
}