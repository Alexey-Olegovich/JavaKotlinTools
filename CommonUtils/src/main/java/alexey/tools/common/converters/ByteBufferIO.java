package alexey.tools.common.converters;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class ByteBufferIO {

    private ByteBuffer byteBuffer;
    private char[] temp = new char[32];



    public ByteBufferIO() {

    }

    public ByteBufferIO(int capacity) {
        byteBuffer = ByteBuffer.allocate(capacity);
    }

    public ByteBufferIO(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }



    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }



    public static long readLong (@NotNull ByteBuffer byteBuffer, boolean optimizePositive) {
        int b = byteBuffer.get();
        long result = b & 0x7F;
        if ((b & 0x80) != 0) {
            b = byteBuffer.get();
            result |= (b & 0x7F) << 7;
            if ((b & 0x80) != 0) {
                b = byteBuffer.get();
                result |= (b & 0x7F) << 14;
                if ((b & 0x80) != 0) {
                    b = byteBuffer.get();
                    result |= (b & 0x7F) << 21;
                    if ((b & 0x80) != 0) {
                        b = byteBuffer.get();
                        result |= (long)(b & 0x7F) << 28;
                        if ((b & 0x80) != 0) {
                            b = byteBuffer.get();
                            result |= (long)(b & 0x7F) << 35;
                            if ((b & 0x80) != 0) {
                                b = byteBuffer.get();
                                result |= (long)(b & 0x7F) << 42;
                                if ((b & 0x80) != 0) {
                                    b = byteBuffer.get();
                                    result |= (long)(b & 0x7F) << 49;
                                    if ((b & 0x80) != 0) {
                                        b = byteBuffer.get();
                                        result |= (long)b << 56;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return optimizePositive ? result : ((result >>> 1) ^ -(result & 1));
    }

    public static int writeInt(@NotNull ByteBuffer byteBuffer, int value, boolean optimizePositive) {
        if (!optimizePositive) value = (value << 1) ^ (value >> 31);
        if (value >>> 7 == 0) {
            byteBuffer.put((byte)value);
            return 1;
        }
        if (value >>> 14 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7));
            return 2;
        }
        if (value >>> 21 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14));
            return 3;
        }
        if (value >>> 28 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14 | 0x80));
            byteBuffer.put((byte)(value >>> 21));
            return 4;
        }
        byteBuffer.put((byte)((value & 0x7F) | 0x80));
        byteBuffer.put((byte)(value >>> 7 | 0x80));
        byteBuffer.put((byte)(value >>> 14 | 0x80));
        byteBuffer.put((byte)(value >>> 21 | 0x80));
        byteBuffer.put((byte)(value >>> 28));
        return 5;
    }

    public static int readInt(@NotNull ByteBuffer byteBuffer, boolean optimizePositive) {
        int b = byteBuffer.get();
        int result = b & 0x7F;
        if ((b & 0x80) != 0) {
            b = byteBuffer.get();
            result |= (b & 0x7F) << 7;
            if ((b & 0x80) != 0) {
                b = byteBuffer.get();
                result |= (b & 0x7F) << 14;
                if ((b & 0x80) != 0) {
                    b = byteBuffer.get();
                    result |= (b & 0x7F) << 21;
                    if ((b & 0x80) != 0) {
                        b = byteBuffer.get();
                        result |= (b & 0x7F) << 28;
                    }
                }
            }
        }
        return optimizePositive ? result : ((result >>> 1) ^ -(result & 1));
    }

    public static int writeIntFlag(@NotNull ByteBuffer byteBuffer, boolean flag, int value, boolean optimizePositive) {
        if (!optimizePositive) value = (value << 1) ^ (value >> 31);
        int first = (value & 0x3F) | (flag ? 0x80 : 0);
        if (value >>> 6 == 0) {
            byteBuffer.put((byte)first);
            return 1;
        }
        if (value >>> 13 == 0) {
            byteBuffer.put((byte)(first | 0x40));
            byteBuffer.put((byte)(value >>> 6));
            return 2;
        }
        if (value >>> 20 == 0) {
            byteBuffer.put((byte)(first | 0x40));
            byteBuffer.put((byte)((value >>> 6) | 0x80));
            byteBuffer.put((byte)(value >>> 13));
            return 3;
        }
        if (value >>> 27 == 0) {
            byteBuffer.put((byte)(first | 0x40));
            byteBuffer.put((byte)((value >>> 6) | 0x80));
            byteBuffer.put((byte)((value >>> 13) | 0x80));
            byteBuffer.put((byte)(value >>> 20));
            return 4;
        }
        byteBuffer.put((byte)(first | 0x40));
        byteBuffer.put((byte)((value >>> 6) | 0x80));
        byteBuffer.put((byte)((value >>> 13) | 0x80));
        byteBuffer.put((byte)((value >>> 20) | 0x80));
        byteBuffer.put((byte)(value >>> 27));
        return 5;
    }

    public static int writeLong(@NotNull ByteBuffer byteBuffer, long value, boolean optimizePositive) {
        if (!optimizePositive) value = (value << 1) ^ (value >> 63);
        if (value >>> 7 == 0) {
            byteBuffer.put((byte)value);
            return 1;
        }
        if (value >>> 14 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7));
            return 2;
        }
        if (value >>> 21 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14));
            return 3;
        }
        if (value >>> 28 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14 | 0x80));
            byteBuffer.put((byte)(value >>> 21));
            return 4;
        }
        if (value >>> 35 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14 | 0x80));
            byteBuffer.put((byte)(value >>> 21 | 0x80));
            byteBuffer.put((byte)(value >>> 28));
            return 5;
        }
        if (value >>> 42 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14 | 0x80));
            byteBuffer.put((byte)(value >>> 21 | 0x80));
            byteBuffer.put((byte)(value >>> 28 | 0x80));
            byteBuffer.put((byte)(value >>> 35));
            return 6;
        }
        if (value >>> 49 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14 | 0x80));
            byteBuffer.put((byte)(value >>> 21 | 0x80));
            byteBuffer.put((byte)(value >>> 28 | 0x80));
            byteBuffer.put((byte)(value >>> 35 | 0x80));
            byteBuffer.put((byte)(value >>> 42));
            return 7;
        }
        if (value >>> 56 == 0) {
            byteBuffer.put((byte)((value & 0x7F) | 0x80));
            byteBuffer.put((byte)(value >>> 7 | 0x80));
            byteBuffer.put((byte)(value >>> 14 | 0x80));
            byteBuffer.put((byte)(value >>> 21 | 0x80));
            byteBuffer.put((byte)(value >>> 28 | 0x80));
            byteBuffer.put((byte)(value >>> 35 | 0x80));
            byteBuffer.put((byte)(value >>> 42 | 0x80));
            byteBuffer.put((byte)(value >>> 49));
            return 8;
        }
        byteBuffer.put((byte)((value & 0x7F) | 0x80));
        byteBuffer.put((byte)(value >>> 7 | 0x80));
        byteBuffer.put((byte)(value >>> 14 | 0x80));
        byteBuffer.put((byte)(value >>> 21 | 0x80));
        byteBuffer.put((byte)(value >>> 28 | 0x80));
        byteBuffer.put((byte)(value >>> 35 | 0x80));
        byteBuffer.put((byte)(value >>> 42 | 0x80));
        byteBuffer.put((byte)(value >>> 49 | 0x80));
        byteBuffer.put((byte)(value >>> 56));
        return 9;
    }

    public static void writeBoolean (@NotNull ByteBuffer byteBuffer, boolean value) {
        byteBuffer.put((byte) (value ? 1 : 0));
    }

    public static boolean readBoolean(@NotNull ByteBuffer byteBuffer) {
        return byteBuffer.get() == 1;
    }

    public static boolean checkIntFlag(@NotNull ByteBuffer byteBuffer) {
        return (byteBuffer.get(byteBuffer.position()) & 0x80) != 0;
    }

    public static int readIntFlag (@NotNull ByteBuffer byteBuffer, boolean optimizePositive) {
        int b = byteBuffer.get();
        int result = b & 0x3F;
        if ((b & 0x40) != 0) {
            b = byteBuffer.get();
            result |= (b & 0x7F) << 6;
            if ((b & 0x80) != 0) {
                b = byteBuffer.get();
                result |= (b & 0x7F) << 13;
                if ((b & 0x80) != 0) {
                    b = byteBuffer.get();
                    result |= (b & 0x7F) << 20;
                    if ((b & 0x80) != 0) {
                        b = byteBuffer.get();
                        result |= (b & 0x7F) << 27;
                    }
                }
            }
        }
        return optimizePositive ? result : ((result >>> 1) ^ -(result & 1));
    }

    public static void writeUTF8(@NotNull ByteBuffer byteBuffer, @NotNull String value) {
        int charCount = value.length();
        if (charCount == 0) {
            byteBuffer.put((byte) 0x80);
            return;
        }
        writeIntFlag(byteBuffer, true, charCount, true);
        int charIndex = 0;
        int c = value.charAt(charIndex);
        while (c <= 127) {
            byteBuffer.put((byte) c);
            if (++charIndex == charCount) return;
            c = value.charAt(charIndex);
        }
        for (; charIndex < charCount; charIndex++) {
            c = value.charAt(charIndex);
            if (c <= 0x007F)
                byteBuffer.put((byte) c);
            else if (c > 0x07FF) {
                byteBuffer.put((byte) (0xE0 | c >> 12 & 0x0F));
                byteBuffer.put((byte) (0x80 | c >> 6 & 0x3F));
                byteBuffer.put((byte) (0x80 | c & 0x3F));
            } else {
                byteBuffer.put((byte) (0xC0 | c >> 6 & 0x1F));
                byteBuffer.put((byte) (0x80 | c & 0x3F));
            }
        }
    }

    public static void writeASCII(@NotNull ByteBuffer byteBuffer, @NotNull String value) {
        int charCount = value.length();
        if (charCount == 0) {
            byteBuffer.put((byte) 0x80);
            return;
        }
        for (int i = 0; i < charCount; ++i) byteBuffer.put((byte)value.charAt(i));
        int position = byteBuffer.position() - 1;
        byteBuffer.put(position, (byte) (byteBuffer.get(position) | 0x80));
    }



    public String readString() {
        if (!checkIntFlag(byteBuffer)) return readASCII();
        int charCount = readIntFlag(byteBuffer,true);
        if (charCount == 0) return "";
        readUTF8Chars(charCount);
        return new String(temp, 0, charCount);
    }

    public String readUTF8(final int charCount) {
        if (charCount < 1) return "";
        readUTF8Chars(charCount);
        return new String(temp, 0, charCount);
    }



    private void readUTF8Chars(final int charCount) {
        if (temp.length < charCount) temp = new char[charCount];
        int charIndex = 0;
        int b = byteBuffer.get();
        while (b > -1) {
            temp[charIndex] = (char) b;
            if (++charIndex == charCount) return;
            b = byteBuffer.get();
        }
        byteBuffer.position(byteBuffer.position() - 1);
        do {
            b = byteBuffer.get() & 0xFF;
            int d = b >> 4;
            if (d < 8) {
                temp[charIndex] = (char) b;
            } else if (d == 12 || d == 13) {
                temp[charIndex] = (char) ((b & 0x1F) << 6 | byteBuffer.get() & 0x3F);
            } else if (d == 14) {
                temp[charIndex] = (char) ((b & 0x0F) << 12 | (byteBuffer.get() & 0x3F) << 6 | byteBuffer.get() & 0x3F);
            }
        } while (++charIndex < charCount);
    }

    @NotNull
    private String readASCII() {
        int charCount = 0, length = temp.length;
        for (; charCount < length; charCount++) {
            int b = byteBuffer.get();
            if ((b & 0x80) == 0x80) {
                temp[charCount] = (char) (b & 0x7F);
                return new String(temp, 0, charCount + 1);
            }
            temp[charCount] = (char) b;
        }
        while (true) {
            int b = byteBuffer.get();
            if (charCount == temp.length) {
                char[] newChars = new char[charCount * 2];
                System.arraycopy(temp, 0, newChars, 0, charCount);
                temp = newChars;
            }
            if ((b & 0x80) == 0x80) {
                temp[charCount] = (char) (b & 0x7F);
                return new String(temp, 0, charCount + 1);
            }
            temp[charCount++] = (char) b;
        }
    }

}
