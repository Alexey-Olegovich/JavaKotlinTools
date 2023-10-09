package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;

public class PairListener<R, W> implements Connection.Listener<R, W> {

    private Connection.Listener<R, W> first;
    private Connection.Listener<R, W> second;



    public PairListener(Connection.Listener<R, W> first, Connection.Listener<R, W> second) {
        setFirst(first);
        setSecond(second);
    }

    public PairListener() {
        first = Connection.Listener.defaultListener();
        second = first;
    }



    public void setFirst(Connection.Listener<R, W> listener) {
        if (listener == null) throw new NullPointerException("Connection can't be null!");
        this.first = listener;
    }

    public void setSecond(Connection.Listener<R, W> listener) {
        if (listener == null) throw new NullPointerException("Connection can't be null!");
        this.second = listener;
    }



    protected Connection.Listener<R, W> getFirst() {
        return first;
    }

    protected Connection.Listener<R, W> getSecond() {
        return second;
    }



    @Override
    public void onDisconnect(@NotNull Connection<R, W> connection) {
        first.onDisconnect(connection);
        second.onDisconnect(connection);
    }

    @Override
    public void onError(@NotNull Connection<R, W> connection, @NotNull Throwable error) {
        first.onError(connection, error);
        second.onError(connection, error);
    }

    @Override
    public void onConnect(@NotNull Connection<R, W> connection) {
        first.onConnect(connection);
        second.onConnect(connection);
    }

    @Override
    public void onWrite(@NotNull Connection<R, W> connection) {
        first.onWrite(connection);
        second.onWrite(connection);
    }

    @Override
    public void onRead(@NotNull Connection<R, W> connection, R message) {
        first.onRead(connection, message);
        second.onRead(connection, message);
    }
}
