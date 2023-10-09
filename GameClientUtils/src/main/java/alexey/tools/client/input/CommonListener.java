package alexey.tools.client.input;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import static alexey.tools.client.input.InputActions.*;

public class CommonListener extends Listener {

    private final Method[] actions = new Method[9];



    public CommonListener(Object target) {
        super(target);
    }



    @Override
    public boolean keyDown(int keycode) {
        return call(keyDown, keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return call(keyUp, keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return call(keyTyped, character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return call(touchDown, screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return call(touchCancelled, screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return call(touchUp, screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return call(touchDragged, screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return call(mouseMoved, screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return call(scrolled, amountX, amountY);
    }



    public boolean isEmpty() {
        for (Method action : actions) if (action != null) return false;
        return true;
    }

    public boolean call(@NotNull final InputActions action, Object... args) {
        return call(actions[action.ordinal()], args);
    }

    public void setAction(@NotNull InputActions action, Method method) {
        actions[action.ordinal()] = method;
    }
}
