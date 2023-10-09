package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ThreadDataProcessor<T> {

    private boolean running = true;
    private T data = null;
    private final Lock lock = new ReentrantLock();
    private final Condition work = lock.newCondition();
    private final Condition done = lock.newCondition();
    private final Thread thread = new Thread(new Worker());



    public ThreadDataProcessor() {
        thread.start();
    }



    abstract protected void process(@NotNull T data);

    public boolean give(T job) {
        lock.lock();
        if (running) {
            while (data != null) try {
                done.await();
            } catch (Throwable ignored) {
                lock.unlock();
                return false;
            }
            data = job;
            work.signal();
            lock.unlock();
            return true;
        } else {
            lock.unlock();
            return false;
        }
    }

    public void await() throws InterruptedException {
        lock.lock();
        if (data != null) try {
            done.await();
        } finally {
            lock.unlock();
        } else lock.unlock();
    }

    public void shutdown() {
        lock.lock();
        if (running) {
            running = false;
            work.signal();
        }
        lock.unlock();
    }

    public boolean isRunning() {
        lock.lock();
        final boolean isRunning = running;
        lock.unlock();
        return isRunning;
    }

    public void interrupt() {
        thread.interrupt();
    }



    private class Worker implements Runnable {

        @Override
        public void run() {
            lock.lock();
            while (running) {
                if (data != null) {
                    lock.unlock();
                    try {
                        process(data);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    lock.lock();
                    data = null;
                    done.signalAll();
                    if (!running) break;
                }
                try {
                    work.await();
                } catch (Throwable ignored) {}
            }
            lock.unlock();
        }
    }
}
