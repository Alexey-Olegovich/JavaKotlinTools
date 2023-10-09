package alexey.tools.common.misc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtils {

    public static <T> T[] minus(final T[] source, final Object element) {
        final int index = indexOf(source, element);
        if (index == -1) return source;
        return unsafeClearAt(source, index);
    }

    @NotNull
    public static <T> T[] unsafeClearAt(@NotNull final T[] source, final int index) {
        final int length = source.length - 1;
        final T[] result = Arrays.copyOf(source, length);
        if (index == length) return result;
        result[index] = source[length];
        return result;
    }

    @Contract(pure = true)
    public static <T> int indexOf(@NotNull final T[] source, final T element) {
        for (int i = 0; i < source.length; i++) if (source[i] == element) return i;
        return -1;
    }

    @NotNull
    public static <T> T[] plus(@NotNull final T[] source, final T element) {
        final int index = source.length;
        final T[] result = Arrays.copyOf(source, index + 1);
        result[index] = element;
        return result;
    }

    @NotNull
    public static <T> T[] plus(@NotNull final T[] source, @NotNull final T[] elements) {
        final int index = source.length;
        final T[] result = Arrays.copyOf(source, index + elements.length);
        System.arraycopy(elements, 0, result, index, elements.length);
        return result;
    }

    public static void unsafeFill(final Object[] a, final int fromIndex, final int toIndex, final Object val) {
        for (int i = fromIndex; i < toIndex; i++) a[i] = val;
    }

    @NotNull
    public static int[] unsafeCopyOf(final int[] source, final int newLength, final int amount) {
        final int[] copy = new int[newLength];
        System.arraycopy(source, 0, copy, 0, amount);
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] unsafeEmptyCopy(@NotNull final T[] source, final int newLength) {
        return (T[]) Array.newInstance(source.getClass().getComponentType(), newLength);
    }
}