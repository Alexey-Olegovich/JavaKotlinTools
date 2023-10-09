package alexey.tools.common.connections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class SocketConnection<R, W> extends SelectableConnection<R, W> {

    final private ByteBuffer output;
    final private ByteBuffer input;
    final private Serialization<R, W> serialization;
    private int currentObjectSize = 0;
    private boolean outputRead = false;
    private SocketChannel socketChannel = null;



    protected SocketConnection(Listener<R, W> listener, Serialization<R, W> serialization,
                               final int input, final int output) {
        super(listener);
        this.serialization = serialization;
        this.input = ByteBuffer.allocate(input);
        this.output = ByteBuffer.allocate(output);
    }

    @SuppressWarnings("unchecked")
    protected SocketConnection(Serialization<R, W> serialization,
                               final int input, final int output) {
        this(Listener.DEFAULT, serialization, input, output);
    }



    public <T> void setOption(SocketOption<T> name, T value) throws IOException {
        socketChannel.setOption(name, value);
    }

    public void send(ByteBuffer source) throws IOException {
        synchronized (output) {
            if (outputRead) {
                output.compact();
                outputRead = false;
            }
            output.put(source);
            write();
        }
    }



    protected void connect(SocketAddress address) throws IOException {
        socketChannel.connect(address);
    }

    protected void connect(String address, final int port) throws IOException {
        connect(new InetSocketAddress(address, port));
    }

    protected void connect(String address) throws IOException {
        connect(toInetSocketAddress(address));
    }

    protected void connect(final int port) throws IOException {
        connect(new InetSocketAddress(port));
    }

    protected boolean finishConnect() throws IOException {
        return socketChannel.finishConnect();
    }

    protected void setSocketChannel(SocketChannel channel) throws IOException {
        if (channel == null) throw new NullPointerException("SocketChannel can't be null!");
        if (socketChannel != null) throw new IllegalStateException("SocketChannel already set!");
        if (!sameChannel(channel)) throw new IllegalStateException("Wrong channel!");
        socketChannel = channel;
        configureConnection();
    }

    @Override
    protected void setSelectionKey(SelectionKey key) {
        if (key == null) throw new NullPointerException("SelectionKey can't be null!");
        if (socketChannel == null || key.channel() == socketChannel)
            super.setSelectionKey(key); else
            throw new IllegalStateException("Wrong channel!");
    }

    protected void configureConnection() throws IOException {
        socketChannel.setOption(StandardSocketOptions.SO_RCVBUF,    1024 * 128);
        socketChannel.setOption(StandardSocketOptions.SO_SNDBUF,    1024 * 128);
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY,  true      );
        socketChannel.setOption(StandardSocketOptions.IP_TOS,       0x14      );
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, false     );
    }

    protected void flush() throws IOException {
        synchronized (output) {
            if (outputRead) {
                do {
                    if (socketChannel.write(output) == 0) return;
                } while (output.hasRemaining());
                outputRead = false;
            } else {
                output.flip();
                do {
                    if (socketChannel.write(output) == 0) { outputRead = true; return; }
                } while (output.hasRemaining());
            }
            output.clear();
            interestOpsAnd(~SelectionKey.OP_WRITE);
        }
        notifyWrite();
    }

    protected void receive() {
        int remaining = input.position();
        if (currentObjectSize == 0) {
            if (remaining < 4) return;
            input.flip();
            receiveObjectSize();
            if (input.hasRemaining()) {
                if ((remaining -= 4) < currentObjectSize) { input.compact(); return; }
            } else { input.clear(); return; }
        } else {
            if (remaining < currentObjectSize) return;
            input.flip();
        }
        int start, deserialized;
        R message;
        do {
            start = input.position();
            message = serialization.read(this, input);
            deserialized = input.position() - start;
            if (deserialized != currentObjectSize) throw new FakeObjectSize();
            notifyRead(message);
            if ((remaining -= deserialized) < 4) { currentObjectSize = 0; break; }
            receiveObjectSize();
        } while ((remaining -= 4) >= currentObjectSize);
        if (remaining == 0)
            input.clear(); else
            input.compact();
    }

    protected boolean read() throws IOException {
        return socketChannel.read(input) == -1;
    }

    protected void close() throws IOException {
        try {
            if (socketChannel != null) socketChannel.close();
        } finally {
            serialization.dispose();
        }
    }



    private void receiveObjectSize() {
        currentObjectSize = input.getInt();
        if (currentObjectSize > input.capacity()) throw new BigObject(currentObjectSize);
    }

    private void write() throws IOException {
        output.flip();
        do {
            if (socketChannel.write(output) != 0) continue;
            outputRead = true;
            interestOpsOr(SelectionKey.OP_WRITE);
            wakeup();
            return;
        } while (output.hasRemaining());
        output.clear();
    }



    @Override
    public void send(W message) throws IOException {
        synchronized (output) {
            if (outputRead) {
                output.compact();
                outputRead = false;
            }
            final int start = output.position();
            try {
                final int begin = start + 4;
                output.position(begin);
                serialization.write(this, output, message);
                final int end = output.position();
                output.position(start);
                output.putInt(end - begin);
                output.position(end);
            } catch (Throwable e) {
                output.position(start);
                throw e;
            }
            write();
        }
    }

    @Override
    public String getAddress() {
        Socket socket = socketChannel.socket();
        return socket.getInetAddress().getHostAddress() + '/' + socket.getPort();
    }

    @Override
    public void initialize(String address) throws IOException {
        throw new IllegalStateException("Already initialized!");
    }



    public static class BigObject extends IllegalStateException {
        final public int size;

        public BigObject(int size) {
            super("Big object (" + size + ")!");
            this.size = size;
        }
    }

    public static class FakeObjectSize extends IllegalStateException {
        public FakeObjectSize() {
            super("Fake object size!");
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static InetSocketAddress toInetSocketAddress(@NotNull final String source) {
        final int separator = source.lastIndexOf(':');
        if (separator == -1) return new InetSocketAddress(Integer.parseUnsignedInt(source));
        return new InetSocketAddress(
                source.substring(0, separator),
                Integer.parseInt(source.substring(separator + 1), 10)
        );
    }
}
