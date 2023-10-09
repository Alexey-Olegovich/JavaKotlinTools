package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;

public class Waiter implements ImmutableWaiter {

    protected final Sync sync;



    public Waiter(final int count) {
        sync = new Sync(count);
    }

    public Waiter() {
        this(1);
    }



    public boolean release() {
        return sync.releaseShared(1);
    }

    public boolean release(final int count) {
        return sync.releaseShared(count);
    }

    public void reset() {
        sync.setCount(1);
    }

    public void reset(final int count) {
        sync.setCount(count);
    }

    public boolean restart() {
        return sync.setCount(1, 0);
    }

    public boolean restart(final int count) {
        return sync.setCount(count, 0);
    }

    public boolean set(final int count, final int condition) {
        return sync.setCount(count, condition);
    }



    @Override
    public void forceAwait() {
        sync.acquireShared(0);
    }

    @Override
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    @Override
    public void await(final int arg) throws InterruptedException {
        sync.acquireSharedInterruptibly(arg);
    }

    @Override
    public boolean await(final long timeout, @NotNull final TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(0, unit.toNanos(timeout));
    }

    @Override
    public boolean await(final int arg, final long timeout, @NotNull final TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(arg, unit.toNanos(timeout));
    }

    @Override
    public boolean isBusy() {
        return sync.getCount() > 0;
    }

    @Override
    public boolean isDone() {
        return sync.getCount() < 1;
    }

    @Override
    public boolean isDone(final int arg) {
        return sync.getCount() <= arg;
    }

    @Override
    public int count() {
        return sync.getCount();
    }

    @Override
    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}
