package alexey.tools.client.input;

import java.lang.reflect.Method;

public class SingletonListener extends Listener {

    public final Method method;



    public SingletonListener(final Object target, final Method method) {
        super(target);
        this.method = method;
    }

    public boolean call(Object... args) {
        return call(method, args);
    }
}
