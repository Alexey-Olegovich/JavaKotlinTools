package alexey.tools.client.input;

import java.lang.reflect.Method;

public class TouchUpListener extends SingletonListener {

    public TouchUpListener(Object target, Method method) {
        super(target, method);
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return call(screenX, screenY, pointer);
    }
}
