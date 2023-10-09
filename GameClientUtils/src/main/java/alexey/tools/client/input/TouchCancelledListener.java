package alexey.tools.client.input;

import java.lang.reflect.Method;

public class TouchCancelledListener extends SingletonListener {

    public TouchCancelledListener(Object target, Method method) {
        super(target, method);
    }



    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return call(screenX, screenY, pointer);
    }
}
