package alexey.tools.client.loaders

import alexey.tools.common.context.ImmutableVariable
import alexey.tools.common.mods.ModLoader
import alexey.tools.server.loaders.DisposableGroup
import com.badlogic.gdx.audio.Sound

class DynamicSoundGroup(modLoader: ModLoader,
                        volume: ImmutableVariable): DisposableGroup<Sound>(DynamicSoundLoader(modLoader, volume), false)