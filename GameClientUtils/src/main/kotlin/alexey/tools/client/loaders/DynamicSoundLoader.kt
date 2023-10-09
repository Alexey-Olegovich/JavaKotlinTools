package alexey.tools.client.loaders

import alexey.tools.common.context.ImmutableVariable
import alexey.tools.common.mods.ModLoader
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle

class DynamicSoundLoader(contentFinder: ModLoader,
                         private val volume: ImmutableVariable): SoundLoader(contentFinder) {

    override fun apply(fileHandle: FileHandle): Sound = DynamicSound(super.apply(fileHandle), volume)
}