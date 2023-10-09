package alexey.tools.common.converters;

import java.io.InputStream;

public abstract class AppendableInputStream extends InputStream {

    protected int limit = 0;
    protected int position = 0;
    protected final byte[] buffer;
    protected boolean end = false;



    public AppendableInputStream(final int bufferSize) {
        buffer = new byte[bufferSize];
    }



    public void end() {
        synchronized (buffer) {
            if (end) return;
            end = true;
            buffer.notifyAll();
        }
    }

    public boolean put(final byte[] data) {
        return put(data, 0, data.length);
    }

    public boolean put(final byte[] data, final int len) {
        return put(data, 0, len);
    }

    abstract public boolean put(final byte[] data, final int off, final int len);



    @Override
    public int available() {
        synchronized (buffer) {
            return limit == position && end ? 0 : Math.max(limit - position, 1);
        }
    }

    @Override
    public void close() {
        synchronized (buffer) {
            close0();
        }
    }



    protected void waitBuffer() {
        try {
            buffer.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void close0() {
        end = true;
        position = limit;
        buffer.notifyAll();
    }
}
