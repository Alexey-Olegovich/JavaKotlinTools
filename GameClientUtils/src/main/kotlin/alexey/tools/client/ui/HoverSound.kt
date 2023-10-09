package alexey.tools.client.ui

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class HoverSound(private val sound: Sound): ClickListener() {

    private var id = -1L



    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        super.enter(event, x, y, pointer, fromActor)
        if (id != -1L) return
        id = sound.play()
    }

    override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        super.exit(event, x, y, pointer, toActor)
        id = -1L
    }
}