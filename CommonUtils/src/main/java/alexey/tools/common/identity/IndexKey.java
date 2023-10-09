package alexey.tools.common.identity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class IndexKey {

    private final int hashCode;
    private final byte[] data;



    public IndexKey(final int value) {
        this.hashCode = value;
        data = null;
    }

    public IndexKey(@NotNull final int[] array) {
        long result = 1;
        final int length = array.length;
        data = new byte[length];
        for (int i = 0; i < length; i++) {
            final int value = array[i];
            result = result * 31 + value;
            data[i] = (byte) (value ^ (value >>> 8));
        }
        hashCode = (int) (result ^ (result >>> 32));
    }

    public IndexKey(final boolean[] checkArray, final int min, final int max, final int length) {
        long result = 1;
        int idx = 0;
        int i = min;
        data = new byte[length];
        for (; i <= max; i++) {
            if (checkArray[i]) {
                result = result * 31 + i;
                data[idx++] = (byte) (i ^ (i >>> 8));
            }
        }
        hashCode = (int) (result ^ (result >>> 32));
    }



    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Arrays.equals(((IndexKey) o).data, data);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public String toString() {
        return "|" + hashCode + ":"
                + Arrays.toString(data) + "|";
    }
}
