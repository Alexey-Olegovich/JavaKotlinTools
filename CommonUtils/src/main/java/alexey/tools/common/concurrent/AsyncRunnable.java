package alexey.tools.common.concurrent;

public interface AsyncRunnable extends Runnable {

    boolean isRunning();

    boolean isWorking();

    boolean isPaused();

    boolean isInterrupted();

    void pause(boolean value);

    void resume();

    void interrupt();

    void interruptIfWorking();

    void await() throws InterruptedException;

    void shutdown();
}
