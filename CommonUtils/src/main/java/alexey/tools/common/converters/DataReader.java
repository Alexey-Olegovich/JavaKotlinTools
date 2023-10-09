package alexey.tools.common.converters;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class DataReader implements Closeable {

    private final InputStream source;
    private final byte[] buffer = new byte[8];
    private char[] temp = new char[16];
    private int position = 0;



    public DataReader(InputStream source) {
        this.source = source;
    }



    public int readUInt8() throws IOException {
        return source.read();
    }

    public int readUInt16() throws IOException {
        fill(2);
        return (getUByte(0) << 8) | getUByte(1);
    }

    public long readUInt32() throws IOException {
        fill(4);
        return ((long) getUByte(0) << 24) |
                ((long) getUByte(1) << 16) |
                ((long) getUByte(2) << 8) |
                getUByte(3);
    }

    public long readUInt64() throws IOException {
        fill(8);
        return ((long) getUByte(0) << 56) |
                ((long) getUByte(1) << 48) |
                ((long) getUByte(2) << 40) |
                ((long) getUByte(3) << 32) |
                ((long) getUByte(4) << 24) |
                ((long) getUByte(5) << 16) |
                ((long) getUByte(6) << 8) |
                getUByte(7);
    }

    public String readUTF8() throws IOException {
        reset();
        readChars();
        return makeString();
    }



    private void readChars() throws IOException {
        int b = source.read();
        while (b < 128) {
            if (b == 0) return;
            put(b);
            b = source.read();
        }
        while (b != 0) {
            int d = b >> 4;
            if (d < 8) {
                put(b);
            } else if (d == 12 || d == 13) {
                put((b & 0x1F) << 6 | source.read() & 0x3F);
            } else if (d == 14) {
                put((b & 0x0F) << 12 | (source.read() & 0x3F) << 6 | source.read() & 0x3F);
            }
            b = source.read();
        }
    }



    private int getUByte(final int index) {
        return buffer[index] & 0xFF;
    }

    private int fill(final int amount) throws IOException {
        return source.read(buffer, 0, amount);
    }

    private void put(final char c) {
        if (position == temp.length) {
            final char[] newTemp = new char[temp.length << 2];
            System.arraycopy(temp, 0, newTemp, 0, position);
            temp = newTemp;
        }
        temp[position++] = c;
    }

    private void put(final int c) {
        put((char) c);
    }

    private void reset() {
        position = 0;
    }

    @NotNull
    @Contract(" -> new")
    private String makeString() {
        return new String(temp, 0, position);
    }



    @Override
    public void close() throws IOException {
        source.close();
    }
}
