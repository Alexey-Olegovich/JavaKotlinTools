package alexey.tools.common.connections;

import alexey.tools.common.collections.IndexedObjectCollection;
import alexey.tools.common.collections.ObjectStorage;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class TCPServer<R, W> extends SelectableConnection<R, W> {

    protected final Selector            selector;
    protected       ServerSocketChannel serverSocketChannel = null;
    protected final Thread              worker;
    protected final Serialization<R, W> serialization;
    protected final ByteBuffer          output;
    protected final int                 inputSize;

    protected final IndexedObjectCollection<TCPRemoteClient<R, W>> connections = new IndexedObjectCollection<>(4);
    protected final ObjectStorage<TCPRemoteClient<R, W>> needDisconnect = new ObjectStorage<>(2);



    public TCPServer(Listener<R, W> listener, Serialization<R, W> serialization,
                     int inputSize, int outputSize, int receiveBufferSize) throws IOException {
        super(listener);
        output = ByteBuffer.allocate(outputSize);
        this.serialization = serialization;
        this.inputSize = inputSize;
        selector = Selector.open();
        try {
            serverSocketChannel = selector.provider().openServerSocketChannel();
            serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, receiveBufferSize);
            setSelectionKey(serverSocketChannel.configureBlocking(false).register(selector, SelectionKey.OP_ACCEPT));
            worker = new Thread(new Worker());
            worker.start();
        } catch (Throwable e) {
            if (serverSocketChannel == null) selector.close(); else close();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public TCPServer(Serialization<R, W> serialization, int inputSize, int outputSize) throws IOException {
        this(Listener.DEFAULT, serialization, inputSize, outputSize, 1024 * 128);
    }

    public TCPServer(Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(serialization, bufferSizes, bufferSizes);
    }

    public TCPServer(Serialization<R, W> serialization) throws IOException {
        this(serialization, 16384);
    }

    public TCPServer(Listener<R, W> listener, Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(listener, serialization, bufferSizes, bufferSizes, 1024 * 128);
    }

    public TCPServer(Listener<R, W> listener, Serialization<R, W> serialization) throws IOException {
        this(listener, serialization, 16384);
    }



    protected void close() throws IOException {
        try { serverSocketChannel.close(); } finally { selector.close(); }
        serialization.dispose();
    }

    protected void remove(TCPRemoteClient<R, W> client) {
        synchronized (connections) { connections.removeReference(client); }
    }



    @Override
    public void send(W message) throws IOException {
        synchronized (output) {
            output.clear();
            output.position(4);
            serialization.write(this, output, message);
            final int end = output.position();
            output.rewind();
            output.putInt(end - 4);
            output.limit(end);
            synchronized (connections) {
                for (TCPRemoteClient connection : connections) {
                    output.rewind();
                    connection.send(output);
                }
            }
        }
    }

    @Override
    public void wantRead(boolean value) {
        if (value)
            synchronized (connections) {
                for (TCPRemoteClient connection : connections)
                    connection.interestOpsOr(SelectionKey.OP_READ);
            }
        else
            synchronized (connections) {
                for (TCPRemoteClient connection : connections)
                    connection.interestOpsAnd(~SelectionKey.OP_READ);
            }
        selector.wakeup();
    }

    @Override
    public void wantConnect(boolean value) {
        wantOperation(SelectionKey.OP_ACCEPT, value);
    }

    @Override
    public boolean wantRead() {
        synchronized (connections) {
            for (TCPRemoteClient connection : connections) if (connection.wantRead()) return true;
        }
        return false;
    }

    @Override
    public boolean wantConnect() {
        return hasOperation(SelectionKey.OP_ACCEPT);
    }

    @Override
    public void initialize(String address) throws IOException {
        serverSocketChannel.bind(SocketConnection.toInetSocketAddress(address));
    }

    public void initialize(int port) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(port));
    }

    @Override
    public String getAddress() {
        ServerSocket socket = serverSocketChannel.socket();
        return socket.getInetAddress().getHostAddress() + '/' + socket.getLocalPort();
    }

    @Override
    public void shutdown() {
        worker.interrupt();
    }

    @Override
    public boolean isTerminated() {
        return !worker.isAlive();
    }

    @Override
    public boolean isTerminating() {
        return worker.isInterrupted() && worker.isAlive();
    }

    @Override
    public boolean isWorking() {
        return !worker.isInterrupted() && worker.isAlive();
    }

    @Override
    public void awaitTermination(final long milliseconds) throws InterruptedException {
        worker.join(milliseconds);
    }



    protected class Worker implements Runnable {

        @Override
        public void run() {
            try {
                try {
                    while (!worker.isInterrupted()) updateServer();
                } finally {
                    try {
                        close();
                    } finally {
                        synchronized (connections) {
                            while (connections.isNotEmpty()) connections.removeLast().disconnect();
                        }
                    }
                }
            } catch (Throwable e) {
                notifyErrorSilently(e);
            }
        }

        protected void updateServer() throws IOException {
            // Update remote clients
            if (selector.select() != 0) {
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    updateClient(selectedKeys.next());
                    selectedKeys.remove();
                }
            }
            // Disconnect clients
            TCPRemoteClient<R, W> connection;
            do {
                synchronized (needDisconnect) {
                    if (needDisconnect.isEmpty()) break;
                    connection = needDisconnect.removeLast();
                }
                if (connection.index == -1) continue;
                remove(connection);
                connection.finish();
            } while (true);
        }

        @SuppressWarnings("unchecked")
        protected void updateClient(@NotNull SelectionKey selectedKey) throws IOException {
            TCPRemoteClient<R, W> connection = (TCPRemoteClient<R, W>) selectedKey.attachment();
            // Connect client
            if (connection == null) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel == null) return;
                try {
                    connection = new TCPRemoteClient<>(needDisconnect, getListener(),
                            serialization.copy(), inputSize, output.capacity());
                    connection.setSelectionKey(socketChannel.configureBlocking(false)
                            .register(selector, SelectionKey.OP_READ, connection));
                    connection.setSocketChannel(socketChannel);
                } catch (Throwable e) {
                    if (connection == null)
                        socketChannel.close(); else
                        connection.close();
                    throw e;
                }
                try {
                    connection.notifyConnect();
                    synchronized (connections) { connections.add(connection); }
                } catch (Throwable e) {
                    connection.error(e);
                }
                return;
            }
            // Read and/or write client data
            try {
                final int readyOps = selectedKey.readyOps();
                if (readyOps != SelectionKey.OP_READ) {
                    connection.flush();
                    if (readyOps == SelectionKey.OP_WRITE) return;
                }
                if (!connection.read()) {
                    connection.receive();
                    return;
                }
            } catch (Throwable e) {
                remove(connection);
                connection.error(e);
                return;
            }
            // End of stream, disconnect client
            remove(connection);
            connection.disconnect();
        }
    }
}
