package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

abstract class FastExecutorMultipleBase extends FastExecutorBase {

    public final int maxThreads;
    protected int activeThreads = 0;



    public FastExecutorMultipleBase() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public FastExecutorMultipleBase(int maxThreads) {
        this.maxThreads = Math.max(maxThreads, 2);
    }



    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        lock.lock();
        if (activeThreads != 0)
            try { return workDone.await(timeout, unit); } finally { lock.unlock(); }
        lock.unlock();
        return true;
    }

    @Override
    public boolean isTerminated() {
        lock.lock();
        final boolean result = !running && activeThreads == 0;
        lock.unlock();
        return result;
    }
}
