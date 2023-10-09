package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FastExecutorCached extends FastExecutorMultipleBase {

    private int totalThreads = 0;



    public FastExecutorCached() {
        super();
    }

    public FastExecutorCached(int maxThreads) {
        super(maxThreads);
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
            if (tasks.size() + activeThreads <= totalThreads) { newCommand.signal(); return; }
            if (totalThreads == maxThreads) return;
            totalThreads++;
        } finally {
            lock.unlock();
        }
        try {
            new Thread(new Worker()).start();
        } catch (Throwable e) {
            lock.lock();
            totalThreads--;
            lock.unlock();
            throw e;
        }
    }

    @Override
    public <T> List<Future<T>> submitAll(@NotNull Collection<? extends Callable<T>> tasks) {
        lock.lock();
        if (!running) {
            lock.unlock();
            return Collections.emptyList();
        }
        List<Future<T>> result;
        int newTotalThreads;
        try {
            result = addAll(tasks);
            newTotalThreads = tasks.size() + activeThreads;
            if (newTotalThreads <= totalThreads) { newCommand.signalAll(); return result; }
            if (totalThreads == maxThreads) return result;
            newTotalThreads = Math.min(newTotalThreads, maxThreads);
            for ( ;totalThreads < newTotalThreads; totalThreads++) new Thread(new Worker()).start();
            return result;
        } finally {
            lock.unlock();
        }
    }



    @Override
    protected void shutdown0() {
        if (!running) return;
        running = false;
        if (activeThreads != totalThreads) newCommand.signalAll();
    }



    private class Worker implements Runnable {
        @Override
        public void run() {
            lock.lock();
            if (running) loop();
            lock.unlock();
        }

        private void loop() {
            activeThreads++;
            do {
                if (tasks.isEmpty()) {
                    workDone();
                    do {
                        try {
                            newCommand.await();
                        } catch (Throwable e) { return; }
                        if (!running) return;
                    } while (tasks.isEmpty());
                    activeThreads++;
                }
                Runnable task = tasks.removeLast();
                lock.unlock();
                try {
                    task.run();
                } catch (Throwable ignored) { }
                Thread.interrupted();
                lock.lock();
            } while (running);
            workDone();
        }

        private void workDone() {
            if (--activeThreads == 0) workDone.signalAll();
        }
    }
}
