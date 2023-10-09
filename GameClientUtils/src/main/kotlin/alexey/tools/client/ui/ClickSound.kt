package alexey.tools.client.ui

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class ClickSound(private val sound: Sound): ChangeListener() {
    override fun changed(event: ChangeEvent, actor: Actor) { sound.play() }
}