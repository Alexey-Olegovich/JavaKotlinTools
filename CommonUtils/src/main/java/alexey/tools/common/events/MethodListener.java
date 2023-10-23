package alexey.tools.common.events;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class MethodListener <P, T> implements NetConsumer<T>, BiConsumer<P, T> {

    private final MethodData method;
    private final Object object;



    public MethodListener(final MethodData method, final Object object) {
        this.method = method;
        this.object = object;
    }



    @Override
    public void accept(@NotNull final T event) {
        method.execute(object, event);
    }

    @Override
    public void accept(int id, @NotNull T event) {
        method.execute(object, id, event);
    }

    @Override
    public void accept(P payload, T event) {
        method.execute(object, payload, event);
    }
}
