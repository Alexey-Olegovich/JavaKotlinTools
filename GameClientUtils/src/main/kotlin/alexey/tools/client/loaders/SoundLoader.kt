package alexey.tools.client.loaders

import alexey.tools.common.mods.ModLoader
import alexey.tools.server.loaders.DisposableGroup
import alexey.tools.server.loaders.FileHandleLoader
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle

open class SoundLoader(contentFinder: ModLoader): FileHandleLoader<Sound>(contentFinder) {
    override fun apply(fileHandle: FileHandle): Sound = Gdx.audio.newSound(fileHandle)

    companion object {
        fun newGroup(modLoader: ModLoader) = DisposableGroup(SoundLoader(modLoader))
    }
}