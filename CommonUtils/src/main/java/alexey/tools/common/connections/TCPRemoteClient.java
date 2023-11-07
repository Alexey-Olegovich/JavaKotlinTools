package alexey.tools.common.connections;

import alexey.tools.common.collections.IndexedObject;
import java.util.Collection;

public class TCPRemoteClient <R, W> extends SocketConnection<R, W> implements IndexedObject {

    protected int index = -1;
    protected byte shutdown = 0;
    protected final Object shutdownLock = new Object();
    protected final Collection<TCPRemoteClient<R, W>> disconnectQueue;



    protected TCPRemoteClient(final Collection<TCPRemoteClient<R, W>> disconnectQueue,
                              final Listener<R, W> listener,
                              final Serialization<R, W> serialization,
                              final int input, final int output) {
        super(listener, serialization, input, output);
        this.disconnectQueue = disconnectQueue;
    }



    public void disconnect() {
        beginShutdown();
        finish();
    }

    public void finish() {
        try {
            close();
            notifyDisconnect();
        } catch (Throwable e) {
            notifyErrorSilently(e);
        }
        notifyShutdown();
    }

    public void error(Throwable e) {
        beginShutdown();
        try {
            close();
        } catch (Throwable closeError) {
            e = closeError;
        }
        notifyErrorSilently(e);
        notifyShutdown();
    }



    private void notifyShutdown() {
        synchronized (shutdownLock) {
            shutdown = 2;
            shutdownLock.notifyAll();
        }
    }

    private void beginShutdown() {
        index = -1;
        synchronized (shutdownLock) {
            shutdown = 1;
        }
    }



    @Override
    public void shutdown() {
        synchronized (shutdownLock) {
            if (shutdown != 0) return;
            shutdown = 1;
        }
        synchronized (disconnectQueue) { disconnectQueue.add(this); }
        wakeup();
    }

    @Override
    public boolean isTerminating() {
        synchronized (shutdownLock) {
            return shutdown == 1;
        }
    }

    @Override
    public boolean isTerminated() {
        synchronized (shutdownLock) {
            return shutdown == 2;
        }
    }

    @Override
    public boolean isWorking() {
        synchronized (shutdownLock) {
            return shutdown == 0;
        }
    }

    @Override
    public void awaitTermination(final long milliseconds) throws InterruptedException {
        synchronized (shutdownLock) {
            if (shutdown == 2) return;
            shutdownLock.wait(milliseconds);
        }
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }
}
