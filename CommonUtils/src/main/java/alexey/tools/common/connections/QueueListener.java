package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class QueueListener <R, W> extends ConnectionWrapper <R, W> implements Connection.Listener <R, W> {

    final LinkedList<W> messages = new LinkedList<>();
    private volatile Listener<R, W> listener = Listener.defaultListener();
    private boolean idle = false;



    public QueueListener(Connection<R, W> connection) {
        super(connection);
    }

    public QueueListener() {
        super();
    }



    @Override
    public void onConnect(@NotNull Connection<R, W> connection) {
        setConnection(connection);
        flush();
        listener.onConnect(this);
    }

    @Override
    public void onWrite(@NotNull Connection<R, W> connection) {
        flush();
        listener.onWrite(this);
    }

    @Override
    public void onDisconnect(@NotNull Connection<R, W> connection) {
        clear();
        listener.onDisconnect(this);
    }

    @Override
    public void onError(@NotNull Connection<R, W> connection, @NotNull Throwable error) {
        clear();
        listener.onError(this, error);
    }

    @Override
    public void onRead(@NotNull Connection<R, W> connection, R message) {
        listener.onRead(this, message);
    }



    @Override
    public void send(W message) {
        if (idle) try {
            super.send(message);
            return;
        } catch (Throwable ignored) {
            idle = false;
        }
        messages.add(message);
    }

    @Override
    public void setListener(Listener<R, W> listener) {
        if (listener == null) throw new NullPointerException("Listener can't be null!");
        this.listener = listener;
    }

    @Override
    public void resetListener() {
        listener = Listener.defaultListener();
    }

    @Override
    public Listener<R, W> getListener() {
        return listener;
    }



    public void flush() {
        idle = true;
        W message = messages.poll();
        try {
            while (message != null) {
                super.send(message);
                message = messages.poll();
            }
        } catch (Throwable ignored) {
            messages.addFirst(message);
            idle = false;
        }
    }



    protected void clear() {
        messages.clear();
        idle = false;
    }
}
