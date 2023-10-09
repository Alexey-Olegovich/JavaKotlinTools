package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AbstractRunnableFuture<T> implements RunnableFuture<T> {

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void run() {

    }



    public static final AbstractRunnableFuture INSTANCE = new AbstractRunnableFuture();

    @SuppressWarnings("unchecked")
    public static <E> AbstractRunnableFuture<E> getInstance() { return INSTANCE; }
}
