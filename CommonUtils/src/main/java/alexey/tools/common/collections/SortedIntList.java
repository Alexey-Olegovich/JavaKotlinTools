package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SortedIntList extends IntList {

    public SortedIntList() {
        this(8);
    }

    public SortedIntList(final int capacity) {
        super(capacity);
    }

    public SortedIntList(@NotNull final int[] other) {
        super(other);
    }

    public SortedIntList(@NotNull final int[] other, int off, int len) {
        super(other, off, len);
    }

    public SortedIntList(@NotNull final ImmutableIntList other) {
        super(other);
    }



    public void sort() {
        Arrays.sort(data);
    }



    @Override
    public int indexOf(int value) {
        return Arrays.binarySearch(data, 0, size, value);
    }

    @Override
    public void clearAt(int index) {
        System.arraycopy(data, index + 1, data, index, --size - index);
    }

    @Override
    public boolean remove(int value) {
        final int index = indexOf(value);
        if (index < 0) return false;
        clearAt(index);
        return true;
    }

    @Override
    public void clear(int value) {
        clearAt(indexOf(value));
    }

    @Override
    public void add(int e) {
        ensureAdd();
        unsafeAdd(e);
    }

    @Override
    public void unsafeAdd(int e) {
        int low = 0;
        int high = size - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = data[mid];
            if (midVal < e) low = mid + 1; else high = mid - 1;
        }
        System.arraycopy(data, low, data, low + 1, size++ - low);
        data[low] = e;
    }

    @Override
    public boolean contains(int e) {
        return indexOf(e) >= 0;
    }
}
