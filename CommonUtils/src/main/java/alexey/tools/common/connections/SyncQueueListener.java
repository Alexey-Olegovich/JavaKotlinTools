package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SyncQueueListener<R, W> extends QueueListener <R, W> {

    public SyncQueueListener(Connection<R, W> connection) {
        super(connection);
    }

    public SyncQueueListener() {
        super();
    }



    @Override
    public void onConnect(@NotNull Connection<R, W> connection) {
        synchronized(messages) {
            setConnection(connection);
            super.flush();
        }
        getListener().onConnect(this);
    }



    @Override
    public void send(W message) throws IOException {
        synchronized(messages) {
            super.send(message);
        }
    }



    public void flush() {
        synchronized (messages) {
            super.flush();
        }
    }

    public boolean isIdle() {
        synchronized (messages) {
            return super.isIdle();
        }
    }



    @Override
    protected void clear() {
        synchronized (messages) {
            super.clear();
        }
    }
}
