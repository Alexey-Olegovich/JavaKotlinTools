package alexey.tools.common.events;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IgnoredMethodData extends MethodData {

    public IgnoredMethodData(@NotNull Class<?> type, @NotNull Method method) {
        super(type, method);
    }



    @Override
    public void execute(@NotNull final Object target, @NotNull final Object data) {
        try {
            method.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(@NotNull final Object target, final int id, @NotNull final Object data) {
        try {
            method.invoke(target, id);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
