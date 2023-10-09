package alexey.tools.client.input;

import java.lang.reflect.Method;

public class KeyDownListener extends SingletonListener {

    public KeyDownListener(Object target, Method method) {
        super(target, method);
    }



    @Override
    public boolean keyDown(int keycode) {
        return call();
    }
}
