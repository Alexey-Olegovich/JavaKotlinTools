package alexey.tools.client.input

import com.badlogic.gdx.InputProcessor

class InputArrayChain(private vararg val inputProcessors: InputProcessor): InputChain() {

    override fun keyDown(keycode: Int): Boolean {
        inputProcessors.forEach { if (it.keyDown(keycode)) return true }
        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        inputProcessors.forEach { if (it.keyUp(keycode)) return true }
        return super.keyUp(keycode)
    }

    override fun keyTyped(character: Char): Boolean {
        inputProcessors.forEach { if (it.keyTyped(character)) return true }
        return super.keyTyped(character)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputProcessors.forEach { if (it.touchDown(screenX, screenY, pointer, button)) return true }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputProcessors.forEach { if (it.touchUp(screenX, screenY, pointer, button)) return true }
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        inputProcessors.forEach { if (it.touchDragged(screenX, screenY, pointer)) return true }
        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        inputProcessors.forEach { if (it.mouseMoved(screenX, screenY)) return true }
        return super.mouseMoved(screenX, screenY)
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        inputProcessors.forEach { if (it.scrolled(amountX, amountY)) return true }
        return super.scrolled(amountX, amountY)
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputProcessors.forEach { if (it.touchCancelled(screenX, screenY, pointer, button)) return true }
        return super.touchCancelled(screenX, screenY, pointer, button)
    }
}