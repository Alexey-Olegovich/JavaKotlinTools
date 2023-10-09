package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SyncVariable <T> implements ConcurrentVariable <T> {
    private volatile T value = null;
    private Supplier<T> initializer;
    private final Object lock;



    public SyncVariable(Supplier<T> initializer) {
        if (initializer == null) throw new NullPointerException();
        this.initializer = initializer;
        lock = this;
    }

    public SyncVariable(Supplier<T> initializer, Object lock) {
        if (initializer == null || lock == null) throw new NullPointerException();
        this.initializer = initializer;
        this.lock = lock;
    }



    @NotNull
    @Override
    public T obtain() {
        T value = this.value;
        if (value != null) return value;
        synchronized (lock) {
            value = this.value;
            if (value != null) return value;
            if (initializer == null) throw new NullPointerException("Cleared!");
            value = initializer.get();
            this.value = value;
            initializer = null;
            return value;
        }
    }

    @Override
    public boolean clear() {
        synchronized (lock) {
            if (initializer == null) return false;
            initializer = null;
            return true;
        }
    }

    @Nullable
    @Override
    public T remove() {
        synchronized (lock) {
            initializer = null;
            return value;
        }
    }

    @Nullable
    @Override
    public T get() {
        return value;
    }
}
