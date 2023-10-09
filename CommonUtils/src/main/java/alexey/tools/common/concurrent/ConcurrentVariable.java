package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConcurrentVariable <T> {
    @NotNull T obtain();
    boolean clear();
    @Nullable T remove();
    @Nullable T get();
}
