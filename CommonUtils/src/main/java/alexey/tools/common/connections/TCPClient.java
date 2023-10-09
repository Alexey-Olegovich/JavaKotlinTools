package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.channels.*;

public class TCPClient<R, W> extends SocketConnection<R, W> implements Connection<R, W> {

    protected final Selector selector;
    protected final Thread worker;



    public TCPClient(Listener<R, W> listener, Serialization<R, W> serialization,
                     int inputSize, int outputSize) throws IOException {
        super(listener, serialization, inputSize, outputSize);
        selector = Selector.open();
        try {
            SocketChannel socketChannel = selector.provider().openSocketChannel();
            setSocketChannel(socketChannel);
            setSelectionKey(socketChannel.configureBlocking(false).register(selector, 0));
            worker = new Thread(new Worker());
            worker.start();
        } catch (Throwable e) {
            close();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public TCPClient(Serialization<R, W> serialization, int inputSize, int outputSize) throws IOException {
        this(Listener.DEFAULT, serialization, inputSize, outputSize);
    }

    public TCPClient(Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(serialization, bufferSizes, bufferSizes);
    }

    public TCPClient(Serialization<R, W> serialization) throws IOException {
        this(serialization, 16384);
    }

    public TCPClient(Listener<R, W> listener, Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(listener, serialization, bufferSizes, bufferSizes);
    }

    public TCPClient(Listener<R, W> listener, Serialization<R, W> serialization) throws IOException {
        this(listener, serialization, 16384);
    }



    public void initialize(int port) throws IOException {
        connect(port);
        setOperations(SelectionKey.OP_CONNECT);
        selector.wakeup();
    }

    public void initialize(@NotNull String address, int port) throws IOException {
        connect(address, port);
        setOperations(SelectionKey.OP_CONNECT);
        selector.wakeup();
    }

    @Override
    public void initialize(@NotNull String address) throws IOException {
        connect(address);
        setOperations(SelectionKey.OP_CONNECT);
        selector.wakeup();
    }

    @Override
    public void shutdown() {
        worker.interrupt();
    }

    @Override
    public void awaitTermination(int milliseconds) throws InterruptedException {
        worker.join(milliseconds);
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
    protected void close() throws IOException {
        try { super.close(); } finally { selector.close(); }
    }



    protected class Worker implements Runnable {

        @Override
        public void run() {
            try {
                try { process(); } finally { close(); }
            } catch (Throwable e) {
                notifyErrorSilently(e);
            }
        }



        private void process() throws IOException {
            do {
                if (worker.isInterrupted()) return;
                if (selector.select() == 0) continue;
                selector.selectedKeys().clear();
                if (finishConnect()) break;
            } while (true);
            setOperations(SelectionKey.OP_READ);
            notifyConnect();
            while (!worker.isInterrupted()) {
                if (selector.select() == 0) continue;
                final int readyOps = getReadyOps();
                selector.selectedKeys().clear();
                if (readyOps != SelectionKey.OP_READ) {
                    flush();
                    if (readyOps == SelectionKey.OP_WRITE) continue;
                }
                if (read()) break;
                receive();
            }
            notifyDisconnect();
        }
    }
}
