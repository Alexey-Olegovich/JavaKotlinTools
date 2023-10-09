package alexey.tools.client.serializers

import alexey.tools.common.loaders.PathGroup
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class DrawableDeserializer(val textures: PathGroup<Texture>): JsonDeserializer<Drawable>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Drawable =
        TextureRegionDrawable(textures.obtainObject(p.text))
}