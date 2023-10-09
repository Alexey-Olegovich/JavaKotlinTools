package alexey.tools.common.events;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface NetConsumer<T> extends Consumer <T> {
    void accept(final int id, @NotNull final T event);
    @Override
    default void accept(@NotNull final T event) { accept(-1, event); }
}
