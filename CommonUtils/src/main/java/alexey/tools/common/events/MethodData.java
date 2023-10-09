package alexey.tools.common.events;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodData {
    public final Class<?> type;
    protected final Method method;



    public MethodData(@NotNull Class<?> type, @NotNull Method method) {
        this.method = method;
        this.type = type;

        method.setAccessible(true);
    }



    public void execute(@NotNull final Object target, @NotNull final Object data) {
        try {
            method.invoke(target, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(@NotNull final Object target, final int id, @NotNull final Object data) {
        try {
            method.invoke(target, id, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}