package alexey.tools.common.misc;

import org.jetbrains.annotations.NotNull;

public interface Injector {
    default void inject(Object target) {

    }

    default void inject(@NotNull final Object[] targets) {
        for (Object target : targets) inject(target);
    }

    Injector DEFAULT = new Injector() {};
}
