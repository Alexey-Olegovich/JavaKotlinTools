package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LazyVariable <T> implements ConcurrentVariable <T> {
    private T value = null;
    private final Object lock;
    private Runnable initializer;



    public LazyVariable(Runnable initializer) {
        if (initializer == null) throw new NullPointerException();
        this.initializer = initializer;
        lock = this;
    }

    public LazyVariable(Runnable initializer, Object lock) {
        if (initializer == null || lock == null) throw new NullPointerException();
        this.initializer = initializer;
        this.lock = lock;
    }



    public boolean set(T value) {
        synchronized (lock) {
            if (initializer == null) return false;
            this.value = value;
            initializer = null;
            lock.notifyAll();
            return true;
        }
    }



    @NotNull
    @Override
    public T obtain() {
        synchronized (lock) {
            if (value != null) return value;
            if (initializer != AbstractRunnableFuture.INSTANCE) {
                if (initializer == null) throw new NullPointerException("Cleared!");
                initializer.run();
                initializer = AbstractRunnableFuture.INSTANCE;
            }
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
        synchronized (lock) {
            return value;
        }
    }
}
