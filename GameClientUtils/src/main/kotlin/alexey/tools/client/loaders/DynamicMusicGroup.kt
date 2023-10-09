package alexey.tools.client.loaders

import alexey.tools.common.context.ImmutableVariable
import alexey.tools.common.mods.ModLoader
import alexey.tools.server.loaders.DisposableGroup
import com.badlogic.gdx.audio.Music

class DynamicMusicGroup(modLoader: ModLoader,
                        volume: ImmutableVariable): DisposableGroup<Music>(DynamicMusicLoader(modLoader, volume), false) {

    init {
        volume.addListener { asMap().values.forEach { it?.volume = volume.toFloat() } }
    }
}