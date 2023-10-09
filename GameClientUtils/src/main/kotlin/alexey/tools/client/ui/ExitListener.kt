package alexey.tools.client.ui

import alexey.tools.server.core.Game
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class ExitListener(private val game: Game<*>): ChangeListener() {
    override fun changed(event: ChangeEvent, actor: Actor) { game.stop() }
}