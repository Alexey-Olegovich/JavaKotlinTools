package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultRunnableFuture<T> extends AbstractRunnableFuture<T> {

    protected final Waiter waiter;



    public DefaultRunnableFuture(final int count) {
        waiter = new Waiter(count);
    }

    public DefaultRunnableFuture() {
        this(1);
    }



    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return waiter.release(2);
    }

    @Override
    public boolean isCancelled() {
        return waiter.count() == -1;
    }

    @Override
    public boolean isDone() {
        return waiter.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        waiter.await();
        return null;
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        waiter.await(timeout, unit);
        return null;
    }

    @Override
    public void run() {
        waiter.release();
    }
}
