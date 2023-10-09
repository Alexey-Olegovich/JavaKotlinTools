package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IntFuture implements Future<Integer> {

    private int value = -1;
    private final Waiter waiter = new Waiter();



    public int getInt() throws InterruptedException {
        waiter.await();
        return value;
    }

    public int getInt(final long timeout, @NotNull final TimeUnit unit) throws InterruptedException {
        waiter.await(timeout, unit);
        return value;
    }

    public void reset() {
        waiter.reset();
    }

    public void set(final int v) {
        value = v;
        waiter.release();
    }



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
        return waiter.isDone();
    }

    @Override
    public Integer get() throws InterruptedException, ExecutionException {
        return getInt();
    }

    @Override
    public Integer get(final long timeout, @NotNull final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return getInt(timeout, unit);
    }
}
