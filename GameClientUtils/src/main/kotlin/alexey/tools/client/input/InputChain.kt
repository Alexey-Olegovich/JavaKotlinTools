package alexey.tools.client.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor

open class InputChain(private val parent: InputProcessor? = Gdx.input.inputProcessor): InputProcessor {
    override fun keyDown(keycode: Int): Boolean {
        return parent?.keyDown(keycode) ?: false
    }

    override fun keyUp(keycode: Int): Boolean {
        return parent?.keyUp(keycode) ?: false
    }

    override fun keyTyped(character: Char): Boolean {
        return parent?.keyTyped(character) ?: false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return parent?.touchDown(screenX, screenY, pointer, button) ?: false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return parent?.touchCancelled(screenX, screenY, pointer, button) ?: false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return parent?.touchUp(screenX, screenY, pointer, button) ?: false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return parent?.touchDragged(screenX, screenY, pointer) ?: false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return parent?.mouseMoved(screenX, screenY) ?: false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return parent?.scrolled(amountX, amountY) ?: false
    }

}