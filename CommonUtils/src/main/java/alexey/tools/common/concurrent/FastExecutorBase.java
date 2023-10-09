package alexey.tools.common.concurrent;

import alexey.tools.common.collections.ObjectList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class FastExecutorBase implements ParallelExecutorService {

    protected final ObjectList<Runnable> tasks = new ObjectList<>();
    protected final Lock lock = new ReentrantLock();
    protected final Condition workDone = lock.newCondition();
    protected final Condition newCommand = lock.newCondition();

    protected boolean running = true;



    @Override
    public boolean isShutdown() {
        lock.lock();
        final boolean result = !running;
        lock.unlock();
        return result;
    }

    @Override
    public void shutdown() {
        lock.lock();
        shutdown0();
        lock.unlock();
    }

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        final List<Runnable> result;
        lock.lock();
        shutdown0();
        result = tasks;
        lock.unlock();
        return result;
    }



    protected <T> List<Future<T>> addAll(@NotNull Collection<? extends Callable<T>> tasks) {
        final int size = tasks.size();
        ObjectList<Future<T>> result = new ObjectList<>(size);
        this.tasks.ensureAdd(size);
        for (Callable<T> task : tasks) {
            FutureTask<T> future = new FutureTask<>(task);
            this.tasks.unsafeAdd(future);
            result.unsafeAdd(future);
        }
        return result;
    }

    protected abstract void shutdown0();
}
