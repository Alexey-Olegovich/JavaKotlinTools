package alexey.tools.client.input;

import com.badlogic.gdx.InputAdapter;
import java.lang.reflect.Method;

public class Listener extends InputAdapter {

    public final Object target;
    Listener next = null;



    public Listener(Object target) {
        this.target = target;
    }



    public boolean call(Method method, Object... args) {
        if (method == null) return false;
        try {
            Object result = method.invoke(target, args);
            if (result instanceof Boolean) return (Boolean) result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
