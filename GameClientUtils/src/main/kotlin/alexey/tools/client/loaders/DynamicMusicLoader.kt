package alexey.tools.client.loaders

import alexey.tools.common.context.ImmutableVariable
import alexey.tools.common.mods.ModLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.files.FileHandle

class DynamicMusicLoader(contentFinder: ModLoader,
                         private val volume: ImmutableVariable): MusicLoader(contentFinder) {

    override fun apply(fileHandle: FileHandle): Music =
        super.apply(fileHandle).also { it.volume = volume.toFloat() }
}