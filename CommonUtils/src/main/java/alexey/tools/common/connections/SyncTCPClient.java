package alexey.tools.common.connections;

import alexey.tools.common.concurrent.Waiter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SyncTCPClient<R, W> extends SocketConnection<R, W> implements SyncConnection<R, W> {

    protected final Selector selector;
    private final Waiter terminated = new Waiter(1);
    private volatile boolean running = false;
    private final ReentrantLock updateLock = new ReentrantLock();



    public SyncTCPClient(Listener<R, W> listener, Serialization<R, W> serialization,
                     int inputSize, int outputSize) throws IOException {
        super(listener, serialization, inputSize, outputSize);
        selector = Selector.open();
        try {
            SocketChannel socketChannel = selector.provider().openSocketChannel();
            setSocketChannel(socketChannel);
            setSelectionKey(socketChannel.configureBlocking(false).register(selector, 0));
        } catch (Throwable e) {
            close();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public SyncTCPClient(Serialization<R, W> serialization, int inputSize, int outputSize) throws IOException {
        this(Listener.DEFAULT, serialization, inputSize, outputSize);
    }

    public SyncTCPClient(Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(serialization, bufferSizes, bufferSizes);
    }

    public SyncTCPClient(Serialization<R, W> serialization) throws IOException {
        this(serialization, 16384);
    }

    public SyncTCPClient(Listener<R, W> listener, Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(listener, serialization, bufferSizes, bufferSizes);
    }

    public SyncTCPClient(Listener<R, W> listener, Serialization<R, W> serialization) throws IOException {
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
        running = false;
        selector.wakeup();
    }

    @Override
    public void awaitTermination(long milliseconds) throws InterruptedException {
        terminated.await(milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isTerminated() {
        return terminated.isDone();
    }

    @Override
    public boolean isTerminating() {
        return !running && terminated.isBusy();
    }

    @Override
    public boolean isWorking() {
        return running;
    }

    @Override
    public int update() {
        return update(0L);
    }

    @Override
    public int update(long milliseconds) {
        if (!updateLock.tryLock()) return 0;
        try {
            if (!running || Thread.currentThread().isInterrupted()) {
                if (terminated.isDone()) return -1;
                close();
                return -1;
            }

            if (selector.select(milliseconds) == 0) return 0;
            final int readyOps = getReadyOps();
            selector.selectedKeys().clear();

            if (readyOps == SelectionKey.OP_CONNECT) {
                if (finishConnect()) {
                    setOperations(SelectionKey.OP_READ);
                    notifyConnect();
                }
                return 1;
            }

            if (readyOps != SelectionKey.OP_READ) {
                flush();
                if (readyOps == SelectionKey.OP_WRITE) return 1;
            }

            if (read()) {
                running = false;
                notifyDisconnect();
                close();
                return -1;
            } else {
                receive();
                return 1;
            }
        } catch (Throwable e1) {
            if (terminated.isBusy()) try {
                close();
            } catch (Throwable e2) {
                notifyError(e2);
                return -1;
            }
            notifyError(e1);
            return -1;
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    protected void close() throws IOException {
        try {
            try {
                super.close();
            } finally {
                selector.close();
            }
        } finally {
            terminated.release();
        }
    }
}
