package alexey.tools.client.input;

import java.lang.reflect.Method;

public class KeyUpListener extends SingletonListener {

    public KeyUpListener(Object target, Method method) {
        super(target, method);
    }



    @Override
    public boolean keyUp(int keycode) {
        return call();
    }
}
