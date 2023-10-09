package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FastExecutorSingle extends FastExecutorBase {

    private boolean executing = false;



    public FastExecutorSingle() {
        new Thread(new Worker()).start();
    }



    @Override
    public void execute(@NotNull Runnable command) {
        lock.lock();
        if (!running) {
            lock.unlock();
            return;
        }
        try {
            tasks.add(command);
            if (executing) return;
            executing = true;
            newCommand.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> List<Future<T>> submitAll(@NotNull Collection<? extends Callable<T>> tasks) {
        lock.lock();
        if (!running) {
            lock.unlock();
            return Collections.emptyList();
        }
        try {
            List<Future<T>> result = addAll(tasks);
            if (executing) return result;
            executing = true;
            newCommand.signal();
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isTerminated() {
        lock.lock();
        final boolean result = !executing && !running;
        lock.unlock();
        return result;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        lock.lock();
        if (executing)
            try { return workDone.await(timeout, unit); } finally { lock.unlock(); }
        lock.unlock();
        return true;
    }



    @Override
    protected void shutdown0() {
        if (!running) return;
        running = false;
        if (!executing) newCommand.signal();
    }



    private class Worker implements Runnable {
        @Override
        public void run() {
            lock.lock();
            loop();
            lock.unlock();
        }

        private void loop() {
            if (!running || !executing && workWait()) { cancelWork(); return; }
            runTask();
            while (running) {
                if (tasks.isEmpty()) {
                    workDone();
                    if (workWait()) { cancelWork(); return; }
                }
                runTask();
            }
            workDone();
        }

        private boolean workWait() {
            try {
                newCommand.await();
            } catch (Throwable ignored) {
                running = false;
            }
            return !running;
        }

        private void runTask() {
            Runnable task = tasks.removeLast();
            lock.unlock();
            try {
                task.run();
            } catch (Throwable ignored) { }
            Thread.interrupted();
            lock.lock();
        }

        private void workDone() {
            executing = false;
            workDone.signalAll();
        }

        private void cancelWork() {
            if (!executing) return;
            executing = false;
            workDone.signalAll();
        }
    }
}
