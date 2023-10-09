package alexey.tools.client.images

import alexey.tools.client.viewport.getWorldCorner
import alexey.tools.common.collections.ObjectList
import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.readBytes
import alexey.tools.server.misc.ResourceFileHandle
import alexey.tools.server.models.EntityModel
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body

fun ByteArray.toTexture(): Texture {
    val pixmap = Pixmap(this, 0, size)
    val texture = Texture(pixmap)
    pixmap.dispose()
    return texture
}

fun Resource.readMusic(): Music {
    return Gdx.audio.newMusic(ResourceFileHandle(this))
}

fun Resource.readTexture(): Texture {
    return readBytes().toTexture()
}

fun newTexture(width: Int, height: Int, color: Color): Texture {
    val pixmap = Pixmap(width, height, Pixmap.Format.RGBA4444)
    pixmap.setColor(color)
    pixmap.fillRectangle(0, 0, width, height)
    val texture = Texture(pixmap)
    pixmap.dispose()
    return texture
}

fun Texture.toTextureRegions(tileWidth: Int, tileHeight: Int): List<TextureRegion> {
    val result = ObjectList<TextureRegion>()
    val textureHeight = height
    val textureWidth = width
    var x = 0
    var y = 0
    while (y < textureHeight) {
        result.add(TextureRegion(this, x, y, tileWidth, tileHeight))
        x += tileWidth
        if (x < textureWidth) continue
        x = 0
        y += tileHeight
    }
    return result
}

fun Texture.toTextureRegions(tileWidth: Int, tileHeight: Int, size: Int): List<TextureRegion> {
    val textureWidth = width
    var x = 0
    var y = 0
    val result = ObjectList<TextureRegion>(size)
    while (result.hasSpace()) {
        result.unsafeAdd(TextureRegion(this, x, y, tileWidth, tileHeight))
        x += tileWidth
        if (x < textureWidth) continue
        x = 0
        y += tileHeight
    }
    return result
}

fun Texture.toArray(tileWidth: Int, tileHeight: Int, size: Int): Array<TextureRegion> {
    val textureWidth = width
    var x = -tileWidth
    var y = 0
    return Array(size) {
        x += tileWidth
        if (x >= textureWidth) {
            x = 0
            y += tileHeight
        }
        TextureRegion(this, x, y, tileWidth, tileHeight)
    }
}

fun Texture.toArray(rows: Int, cols: Int): Array<TextureRegion> {
    val textureHeight = height
    val textureWidth = width
    val tileWidth = textureWidth / cols
    val tileHeight = textureHeight / rows
    var x = -tileWidth
    var y = 0
    return Array(rows * cols) {
        x += tileWidth
        if (x >= textureWidth) {
            x = 0
            y += tileHeight
        }
        TextureRegion(this, x, y, tileWidth, tileHeight)
    }
}

fun Texture.toArray(cols: Int): Array<TextureRegion> {
    val textureHeight = height
    val tileWidth = width / cols
    var x = -tileWidth
    return Array(cols) {
        x += tileWidth
        TextureRegion(this, x, 0, tileWidth, textureHeight)
    }
}

fun Array<TextureRegion>.toAnimation(frameDuration: Float = 0.1f,
                                     playMode: Animation.PlayMode = Animation.PlayMode.LOOP) =
    Animation(frameDuration, *this).also { it.playMode = playMode }

fun Sprite.setTexture(texture: Texture, width: Float) {
    this.texture = texture
    val w = texture.width
    val h = texture.height
    setRegion(0, 0, w, h)
    setSize(width, h * width / w)
}

fun Sprite.setTexture(height: Float, texture: Texture) {
    this.texture = texture
    val w = texture.width
    val h = texture.height
    setRegion(0, 0, w, h)
    setSize(w * height / h, height)
}

fun Sprite.resizeWidth(width: Float) = setSize(width, width * height / this.width)

fun Sprite.resizeHeight(height: Float) = setSize(height * width / this.height, height)

fun SpriteBatch.drawFromCenter(textureRegion: TextureRegion, body: Body, width: Float, height: Float) {
    val position = body.position
    val halfWidth = width / 2F
    val halfHeight = height / 2F
    draw(textureRegion,
        position.x - halfWidth, position.y - halfHeight,
        halfWidth, halfHeight,
        width, height,
        1F, 1F,
        body.angle * MathUtils.radiansToDegrees)
}

fun SpriteBatch.drawFromCenter(textureRegion: TextureRegion, body: Body, entityModel: EntityModel) =
    drawFromCenter(textureRegion, body, entityModel.width, entityModel.height)

fun SpriteBatch.drawFromCenter(textureRegion: TextureRegion, entityModel: EntityModel) {
    val halfWidth = entityModel.width / 2F
    val halfHeight = entityModel.height / 2F
    draw(textureRegion,
        entityModel.x - halfWidth, entityModel.y - halfHeight,
        halfWidth, halfHeight,
        entityModel.width, entityModel.height,
        1F, 1F,
        -entityModel.angle)
}

fun SpriteBatch.drawFromLeftDown(textureRegion: TextureRegion, body: Body, width: Float, height: Float) {
    val position = body.position
    draw(textureRegion,
        position.x, position.y,
        0F, 0F,
        width, height,
        1F, 1F,
        body.angle * MathUtils.radiansToDegrees)
}

fun SpriteBatch.drawFromLeftDown(textureRegion: TextureRegion, body: Body, entityModel: EntityModel) =
    drawFromLeftDown(textureRegion, body, entityModel.width, entityModel.height)

fun SpriteBatch.drawFromLeftDown(textureRegion: TextureRegion, entityModel: EntityModel) {
    draw(textureRegion,
        entityModel.x, entityModel.y,
        0F, 0F,
        entityModel.width, entityModel.height,
        1F, 1F,
        -entityModel.angle)
}

fun SpriteBatch.fillDraw(textureRegion: TextureRegion, camera: Camera) {
    val corner = camera.getWorldCorner()
    val ratio = textureRegion.regionWidth / textureRegion.regionHeight.toFloat()
    if (ratio > camera.viewportWidth / camera.viewportHeight)
        draw(textureRegion, corner.x, corner.y, camera.viewportHeight * ratio, camera.viewportHeight) else
        draw(textureRegion, corner.x, corner.y, camera.viewportWidth, camera.viewportWidth / ratio)
}