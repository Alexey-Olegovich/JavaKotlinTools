package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;

public class ListenerWrapper <R, W> implements Connection.Listener<R, W> {

    private Connection.Listener<R, W> listener;



    public ListenerWrapper(Connection.Listener<R, W> listener) {
        setListener(listener);
    }

    public ListenerWrapper() {
        listener = Connection.Listener.defaultListener();
    }



    public void setListener(Connection.Listener<R, W> listener) {
        if (listener == null) throw new NullPointerException("Listener can't be null!");
        this.listener = listener;
    }



    protected Connection.Listener<R, W> getListener() {
        return listener;
    }



    @Override
    public void onDisconnect(@NotNull Connection<R, W> connection) {
        listener.onDisconnect(connection);
    }

    @Override
    public void onError(@NotNull Connection<R, W> connection, @NotNull Throwable error) {
        listener.onError(connection, error);
    }

    @Override
    public void onConnect(@NotNull Connection<R, W> connection) {
        listener.onConnect(connection);
    }

    @Override
    public void onWrite(@NotNull Connection<R, W> connection) {
        listener.onWrite(connection);
    }

    @Override
    public void onRead(@NotNull Connection<R, W> connection, R message) {
        listener.onRead(connection, message);
    }
}
