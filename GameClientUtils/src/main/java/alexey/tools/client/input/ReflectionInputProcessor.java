package alexey.tools.client.input;

import com.badlogic.gdx.InputProcessor;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Method;

public class ReflectionInputProcessor implements InputProcessor {

    private final Listener[] keyListeners = new Listener[256];
    private final Listener[] mouseListeners = new Listener[5];
    private Listener commonListeners = null;



    @Override
    public boolean keyDown(int keycode) {
        Listener listener = keyListeners[keycode];
        while (listener != null) {
            if (listener.keyDown(keycode)) return true;
            listener = listener.next;
        }
        listener = commonListeners;
        while (listener != null) {
            if (listener.keyDown(keycode)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Listener listener = keyListeners[keycode];
        while (listener != null) {
            if (listener.keyUp(keycode)) return true;
            listener = listener.next;
        }
        listener = commonListeners;
        while (listener != null) {
            if (listener.keyUp(keycode)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Listener listener = commonListeners;
        while (listener != null) {
            if (listener.keyTyped(character)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Listener listener = mouseListeners[button];
        while (listener != null) {
            if (listener.touchDown(screenX, screenY, pointer, button)) return true;
            listener = listener.next;
        }
        listener = commonListeners;
        while (listener != null) {
            if (listener.touchDown(screenX, screenY, pointer, button)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Listener listener = mouseListeners[button];
        while (listener != null) {
            if (listener.touchUp(screenX, screenY, pointer, button)) return true;
            listener = listener.next;
        }
        listener = commonListeners;
        while (listener != null) {
            if (listener.touchUp(screenX, screenY, pointer, button)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        Listener listener = mouseListeners[button];
        while (listener != null) {
            if (listener.touchCancelled(screenX, screenY, pointer, button)) return true;
            listener = listener.next;
        }
        listener = commonListeners;
        while (listener != null) {
            if (listener.touchCancelled(screenX, screenY, pointer, button)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Listener listener = commonListeners;
        while (listener != null) {
            if (listener.touchDragged(screenX, screenY, pointer)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Listener listener = commonListeners;
        while (listener != null) {
            if (listener.mouseMoved(screenX, screenY)) return true;
            listener = listener.next;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Listener listener = commonListeners;
        while (listener != null) {
            if (listener.scrolled(amountX, amountY)) return true;
            listener = listener.next;
        }
        return false;
    }



    public void addKeyListener(@NotNull final Listener listener, final int code) {
        listener.next = keyListeners[code];
        keyListeners[code] = listener;
    }

    public void addListener(final Object target,
                            final Method method,
                            final int code,
                            @NotNull final InputActions action) {

        switch (action) {
            case keyDown: addKeyListener(new KeyDownListener(target, method), code); break;
            case keyUp: addKeyListener(new KeyUpListener(target, method), code); break;
            case touchDown: addMouseListener(new TouchDownListener(target, method), code); break;
            case touchUp: addMouseListener(new TouchUpListener(target, method), code); break;
            case touchCancelled: addMouseListener(new TouchCancelledListener(target, method), code);
        }
    }

    public void addMouseListener(@NotNull final Listener listener, final int code) {
        listener.next = mouseListeners[code];
        mouseListeners[code] = listener;
    }

    public void addListener(@NotNull final Listener listener) {
        listener.next = commonListeners;
        commonListeners = listener;
    }

    public void addListeners(@NotNull Object target) {
        CommonListener listener = new CommonListener(target);
        for (Method method : target.getClass().getDeclaredMethods()) {
            InputAction inputAction = method.getDeclaredAnnotation(InputAction.class);
            if (inputAction == null) continue;
            method.setAccessible(true);
            final int code = inputAction.code();
            InputActions action = inputAction.action();
            if (code == -1) {
                if (action == InputActions.none) action = InputActions.valueOf(method.getName());
                listener.setAction(action, method);
            } else addListener(target, method, code, action);
        }
        if (!listener.isEmpty()) addListener(listener);
    }
}
