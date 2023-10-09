package alexey.tools.common.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract public class ExecutorAsyncRunnable implements AsyncRunnable {

    private final ExecutorService executor;
    private final Waiter waiter = new Waiter(0);
    private final Worker worker = new Worker();

    private volatile boolean mustExecute = true;



    public ExecutorAsyncRunnable(ExecutorService executor) {
        this.executor = executor;
    }

    public ExecutorAsyncRunnable() {
        this(Executors.newSingleThreadExecutor());
    }



    @Override
    public void resume() {
        if (waiter.restart()) executor.execute(worker);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public void await() throws InterruptedException {
        waiter.await();
    }

    @Override
    public void pause(boolean value) {
        mustExecute = !value;
    }

    @Override
    public boolean isPaused() {
        return !mustExecute;
    }

    @Override
    public boolean isRunning() {
        return !executor.isShutdown();
    }

    @Override
    public boolean isWorking() {
        return waiter.isBusy();
    }

    @Override
    public void interrupt() {
        // Unsupported operation
    }

    @Override
    public void interruptIfWorking() {
        // Unsupported operation
    }

    @Override
    public boolean isInterrupted() {
        return false; // Unsupported operation
    }



    private class Worker implements Runnable {
        @Override
        public void run() {
            while (mustExecute) ExecutorAsyncRunnable.this.run();
            waiter.release();
        }
    }
}
