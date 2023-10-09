package alexey.tools.common.converters;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class DataWriter {

    private final OutputStream destination;



    public DataWriter(OutputStream destination) {
        this.destination = destination;
    }



    public void writeUInt8(final int value) throws IOException {
        destination.write(value);
    }

    public void writeUInt16(final int value) throws IOException {
        destination.write(value >>> 8);
        destination.write(value & 0xFF);
    }

    public void writeUInt32(final long value) throws IOException {
        destination.write((int) (value >>> 24));
        destination.write((int) ((value >>> 16) & 0xFF));
        destination.write((int) ((value >>> 8) & 0xFF));
        destination.write((int) (value & 0xFF));
    }

    public void writeUInt64(final long value) throws IOException {
        destination.write((int) (value >>> 56));
        destination.write((int) ((value >>> 48) & 0xFF));
        destination.write((int) ((value >>> 40) & 0xFF));
        destination.write((int) ((value >>> 32) & 0xFF));
        destination.write((int) ((value >>> 24) & 0xFF));
        destination.write((int) ((value >>> 16) & 0xFF));
        destination.write((int) ((value >>> 8) & 0xFF));
        destination.write((int) (value & 0xFF));
    }

    public void writeUTF8(@NotNull final String value) throws IOException {
        int charCount = value.length();
        if (charCount == 0) {
            destination.write(0);
            return;
        }
        int charIndex = 0;
        int c = value.charAt(charIndex);
        while (c <= 127) {
            destination.write(c);
            if (++charIndex == charCount) {
                destination.write(0);
                return;
            }
            c = value.charAt(charIndex);
        }
        for (; charIndex < charCount; charIndex++) {
            c = value.charAt(charIndex);
            if (c <= 0x007F)
                destination.write(c);
            else if (c > 0x07FF) {
                destination.write(0xE0 | c >> 12 & 0x0F);
                destination.write(0x80 | c >> 6 & 0x3F);
                destination.write(0x80 | c & 0x3F);
            } else {
                destination.write(0xC0 | c >> 6 & 0x1F);
                destination.write(0x80 | c & 0x3F);
            }
        }
        destination.write(0);
    }
}
