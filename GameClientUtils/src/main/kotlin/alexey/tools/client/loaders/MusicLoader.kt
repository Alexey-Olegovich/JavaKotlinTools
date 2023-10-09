package alexey.tools.client.loaders

import alexey.tools.common.mods.ModLoader
import alexey.tools.server.loaders.DisposableGroup
import alexey.tools.server.loaders.FileHandleLoader
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.files.FileHandle

open class MusicLoader(contentFinder: ModLoader): FileHandleLoader<Music>(contentFinder) {
    override fun apply(fileHandle: FileHandle): Music = Gdx.audio.newMusic(fileHandle)

    companion object {
        fun newGroup(modLoader: ModLoader) = DisposableGroup(MusicLoader(modLoader))
    }
}