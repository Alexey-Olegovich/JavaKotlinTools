package alexey.tools.common.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ThreadAsyncRunnable implements AsyncRunnable {

    private boolean running = true;
    private boolean paused = true;
    private boolean working = false;

    private byte state = IDLE;

    private final Lock lock = new ReentrantLock();
    private final Condition work = lock.newCondition();
    private final Condition done = lock.newCondition();
    private final Thread thread = new Thread(new Worker());



    public ThreadAsyncRunnable() {
        thread.start();
    }



    @Override
    public boolean isRunning() {
        lock.lock();
        final boolean isRunning = running;
        lock.unlock();
        return isRunning;
    }

    @Override
    public boolean isWorking() {
        lock.lock();
        final boolean isWorking = working;
        lock.unlock();
        return isWorking;
    }

    @Override
    public boolean isPaused() {
        lock.lock();
        final boolean isPaused = paused;
        lock.unlock();
        return isPaused;
    }

    @Override
    public boolean isInterrupted() {
        lock.lock();
        final int state = this.state;
        lock.unlock();
        return state == INTERRUPTED;
    }

    @Override
    public void pause(boolean value) {
        lock.lock();
        paused = value;
        lock.unlock();
    }

    @Override
    public void resume() {
        lock.lock();
        if (running && !working) {
            working = true;
            work.signal();
        }
        lock.unlock();
    }

    @Override
    public void interrupt() {
        lock.lock();
        interruptExecution();
        lock.unlock();
    }

    @Override
    public void interruptIfWorking() {
        lock.lock();
        if (working) interruptExecution();
        lock.unlock();
    }

    @Override
    public void await() throws InterruptedException {
        lock.lock();
        if (working) {
            try {
                done.await();
            } finally {
                lock.unlock();
            }
        } else lock.unlock();
    }

    @Override
    public void shutdown() {
        lock.lock();
        if (running) {
            running = false;
            if (!working) work.signal();
        }
        lock.unlock();
    }



    private void interruptExecution() {
        switch (state) {
            case EXECUTING: try { thread.interrupt(); } catch (Throwable ignored) { }
            case IDLE:      state = INTERRUPTED;
        }
    }

    protected void afterShutdown() {}



    private class Worker implements Runnable {
        @Override
        public void run() {
            lock.lock();
            main();
            if (working) workDone();
            lock.unlock();
            afterShutdown();
        }

        private void main() {
            if (!running || !working && workWait()) return;
            while (state == INTERRUPTED) {
                workDone();
                state = IDLE;
                if (workWait()) return;
            }
            state = EXECUTING;
            runTask();
            while (running) {
                block: {
                    if (state == INTERRUPTED) {
                        Thread.interrupted();
                    } else {
                        if (!paused) break block;
                    }
                    do {
                        workDone();
                        state = IDLE;
                        if (workWait()) return;
                    } while (state == INTERRUPTED);
                    state = EXECUTING;
                }
                runTask();
            }
        }

        private boolean workWait() {
            try {
                work.await();
            } catch (Throwable ignored) {}
            return !running;
        }

        private void workDone() {
            working = false;
            done.signalAll();
        }

        private void runTask() {
            lock.unlock();
            try {
                ThreadAsyncRunnable.this.run();
            } catch (Throwable error) {
                error.printStackTrace();
            }
            lock.lock();
        }
    }



    private static final byte INTERRUPTED = -1;
    private static final byte IDLE = 0;
    private static final byte EXECUTING = 1;
}
