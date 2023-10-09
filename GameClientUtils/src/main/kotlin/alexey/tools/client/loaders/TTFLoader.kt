package alexey.tools.client.loaders

import alexey.tools.common.mods.ModLoader
import alexey.tools.server.loaders.FileHandleLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

class TTFLoader(contentFinder: ModLoader,
                val size: Int = 28,
                val color: Color = Color.LIGHT_GRAY): FileHandleLoader<BitmapFont>(contentFinder) {
    override fun apply(fileHandle: FileHandle): BitmapFont = FreeTypeFontGenerator(fileHandle)
        .generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().also {
            it.size = size
            it.color = color
            it.characters = RUSSIAN_CHARS
        })

    companion object {
        const val RUSSIAN_CHARS = FreeTypeFontGenerator.DEFAULT_CHARS +
                "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
    }
}
