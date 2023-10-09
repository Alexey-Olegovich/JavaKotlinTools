package alexey.tools.client.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class HideListener(private val target: Actor): ChangeListener() {
    override fun changed(event: ChangeEvent, actor: Actor) { target.isVisible = false }
}