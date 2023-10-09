package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;

public class ReplaceListener <R, W> implements Connection.Listener <R, W> {

    private final Connection.Listener<R, W> listener;
    private final Connection<R, W> connection;



    public ReplaceListener(Connection<R, W> connection, Connection.Listener<R, W> listener) {
        if (connection == null || listener == null) throw new NullPointerException();
        this.connection = connection;
        this.listener = listener;
    }



    @Override
    public void onRead(@NotNull Connection<R, W> connection, R message) {
        listener.onRead(this.connection, message);
    }

    @Override
    public void onError(@NotNull Connection<R, W> connection, @NotNull Throwable error) {
        listener.onError(this.connection, error);
    }

    @Override
    public void onWrite(@NotNull Connection<R, W> connection) {
        listener.onWrite(this.connection);
    }

    @Override
    public void onDisconnect(@NotNull Connection<R, W> connection) {
        listener.onDisconnect(this.connection);
    }

    @Override
    public void onConnect(@NotNull Connection<R, W> connection) {
        listener.onConnect(this.connection);
    }
}
