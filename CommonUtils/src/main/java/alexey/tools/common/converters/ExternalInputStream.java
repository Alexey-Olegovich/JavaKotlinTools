package alexey.tools.common.converters;

import org.jetbrains.annotations.NotNull;

public class ExternalInputStream extends AppendableInputStream {

    public ExternalInputStream(final int bufferSize) {
        super(bufferSize);
    }



    @Override
    public boolean put(final byte[] data, final int off, final int len) {
        if (len > buffer.length ||
            len == 0 ||
            off < 0 ||
            off + len > data.length) return false;
        synchronized (buffer) {
            if (end) return false;
            if (len < 0) {
                end = true;
                buffer.notifyAll();
                return false;
            }
            int size = limit - position;
            while (buffer.length - size < len) {
                waitBuffer();
                if (end) return false;
                size = limit - position;
            }
            position = 0;
            if (size == 0) {
                limit = len;
                System.arraycopy(data, off, buffer, 0, len);
            } else {
                limit = len + size;
                System.arraycopy(buffer, position, buffer, 0, size);
                System.arraycopy(data, off, buffer, size, len);
            }
            buffer.notifyAll();
            return true;
        }
    }

    @Override
    public int read() {
        synchronized (buffer) {
            while (position == limit) {
                if (end) return -1;
                waitBuffer();
            }
            final int result = buffer[position++] & 0xFF;
            buffer.notifyAll();
            return result;
        }
    }

    @Override
    public int read(@NotNull final byte[] b, final int off, final int len) {
        synchronized (buffer) {
            while (position == limit) {
                if (end) return -1;
                waitBuffer();
            }
            final int amount = Math.min(len, limit - position);
            System.arraycopy(buffer, position, b, off, amount);
            position += amount;
            buffer.notifyAll();
            return amount;
        }
    }

    @Override
    public long skip(final long n) {
        if (n < 1L) return 0L;
        synchronized (buffer) {
            while (position == limit) {
                if (end) return 0L;
                waitBuffer();
            }
            long total = n;
            do {
                final long len = limit - position;
                if (total < len) {
                    position += total;
                    buffer.notifyAll();
                    return n;
                } else {
                    position = limit;
                    total -= len;
                    buffer.notifyAll();
                    do {
                        if (end) return n - total;
                        waitBuffer();
                    } while (position == limit);
                }
            } while (total > 0L);
            return n - total;
        }
    }
}
