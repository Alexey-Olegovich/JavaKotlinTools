package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FastExecutorFixed extends FastExecutorMultipleBase {


    public FastExecutorFixed() {
        super();
        init();
    }

    public FastExecutorFixed(int maxThreads) {
        super(maxThreads);
        init();
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
            if (activeThreads == maxThreads) return;
            activeThreads++;
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
            if (activeThreads == maxThreads) return result;
            activeThreads++;
            newCommand.signal();
            return result;
        } finally {
            lock.unlock();
        }
    }



    @Override
    protected void shutdown0() {
        if (!running) return;
        running = false;
        if (activeThreads == maxThreads) return;
        activeThreads = maxThreads;
        newCommand.signalAll();
    }



    private void init() {
        activeThreads = maxThreads;
        for (int i = 0; i < maxThreads; i++) new Thread(new Worker()).start();
    }



    private class Worker implements Runnable {

        @Override
        public void run() {
            lock.lock();
            while (running) {
                while (tasks.isEmpty()) {
                    if (--activeThreads == 0) workDone.signalAll();
                    try {
                        newCommand.await();
                    } catch (Throwable e) { cancel(); return; }
                    if (!running) { cancel(); return; }
                }
                Runnable task = tasks.removeLast();
                lock.unlock();
                try {
                    task.run();
                } catch (Throwable ignored) { }
                Thread.interrupted();
                lock.lock();
            }
            cancel();
        }

        private void cancel() {
            if (--activeThreads == 0) workDone.signalAll();
            lock.unlock();
        }
    }
}
