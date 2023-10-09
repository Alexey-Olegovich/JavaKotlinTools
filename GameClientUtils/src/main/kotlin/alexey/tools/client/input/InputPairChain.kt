package alexey.tools.client.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor

class InputPairChain(private val inputProcessor: InputProcessor,
                     parent: InputProcessor? = Gdx.input.inputProcessor): InputChain(parent) {
    
    override fun keyDown(keycode: Int): Boolean {
        if (inputProcessor.keyDown(keycode)) return true
        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        if (inputProcessor.keyUp(keycode)) return true
        return super.keyUp(keycode)
    }

    override fun keyTyped(character: Char): Boolean {
        if (inputProcessor.keyTyped(character)) return true
        return super.keyTyped(character)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (inputProcessor.touchDown(screenX, screenY, pointer, button)) return true
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (inputProcessor.touchUp(screenX, screenY, pointer, button)) return true
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (inputProcessor.touchDragged(screenX, screenY, pointer)) return true
        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (inputProcessor.mouseMoved(screenX, screenY)) return true
        return super.mouseMoved(screenX, screenY)
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        if (inputProcessor.scrolled(amountX, amountY)) return true
        return super.scrolled(amountX, amountY)
    }


}