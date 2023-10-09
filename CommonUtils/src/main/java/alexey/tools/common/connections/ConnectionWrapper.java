package alexey.tools.common.connections;

import java.io.IOException;

public class ConnectionWrapper <R, W> implements Connection<R, W>  {

    private Connection<R, W> connection;



    public ConnectionWrapper(Connection<R, W> connection) {
        setConnection(connection);
    }

    public ConnectionWrapper() {
        connection = Connection.defaultConnection();
    }



    public void setConnection(Connection<R, W> connection) {
        if (connection == null) throw new NullPointerException("Connection can't be null!");
        this.connection = connection;
    }



    protected Connection<R, W> getConnection() {
        return connection;
    }



    @Override
    public void send(W message) throws IOException {
        connection.send(message);
    }

    @Override
    public boolean isTerminated() {
        return connection.isTerminated();
    }

    @Override
    public boolean isTerminating() {
        return connection.isTerminating();
    }

    @Override
    public boolean isWorking() {
        return connection.isWorking();
    }

    @Override
    public boolean wantConnect() {
        return connection.wantConnect();
    }

    @Override
    public boolean wantRead() {
        return connection.wantRead();
    }

    @Override
    public Connection.Listener<R, W> getListener() {
        return connection.getListener();
    }

    @Override
    public Object attachment() {
        return connection.attachment();
    }

    @Override
    public String getAddress() {
        return connection.getAddress();
    }

    @Override
    public void attach(Object ob) {
        connection.attach(ob);
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        connection.awaitTermination();
    }

    @Override
    public void awaitTermination(final int milliseconds) throws InterruptedException {
        connection.awaitTermination(milliseconds);
    }

    @Override
    public void initialize(String address) throws IOException {
        connection.initialize(address);
    }

    @Override
    public void resetListener() {
        connection.resetListener();
    }

    @Override
    public void setListener(Listener<R, W> listener) {
        connection.setListener(listener);
    }

    @Override
    public void shutdown() {
        connection.shutdown();
    }

    @Override
    public void wantConnect(final boolean value) {
        connection.wantConnect(value);
    }

    @Override
    public void wantRead(final boolean value) {
        connection.wantRead(value);
    }
}
