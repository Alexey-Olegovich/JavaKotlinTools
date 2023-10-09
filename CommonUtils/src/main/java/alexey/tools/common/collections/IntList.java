package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class IntList implements ImmutableIntList {

    protected int size;
    protected int[] data;
    transient protected final IntIterator iterator = createIterator();



    protected IntList(final int[] data, final int size) {
        this.data = data;
        this.size = size;
    }



    public IntList() {
        this(8);
    }

    public IntList(final int capacity) {
        this(new int[capacity], 0);
    }

    public IntList(@NotNull final int[] other) {
        this(other.clone(), other.length);
    }

    public IntList(@NotNull final int[] other, int off, int len) {
        this(new int[len], len);
        System.arraycopy(other, off, data, 0, size);
    }

    public IntList(@NotNull final ImmutableIntList other) {
        data = other.toArray();
        size = data.length;
    }



    public int[] getData() {
        return data;
    }

    public int safeRemoveAt(final int index) {
        rangeCheck(index);
        return removeAt(index);
    }

    public boolean hasSpace() {
        return size < data.length;
    }

    public void incSize() {
        ensureAdd(); size++;
    }

    public void safeClearAt(final int index) {
        if (index >= size || index < 0) outOfBounds(index);
        clearAt(index);
    }

    public void clearAt(final int index) {
        data[index] = data[--size];
    }

    public void clear(final int value) {
        for (int i = 0; size > i; i++)
            if (value == data[i]) data[i] = data[--size];
    }

    public int removeAt(final int index) {
        final int e = data[index];
        clearAt(index);
        return e;
    }

    public int safeRemoveLast() {
        if (size < 1) throw new IllegalStateException("IntList is empty!");
        return removeLast();
    }

    public int removeLast() {
        return data[--size];
    }

    public boolean remove(final int value) {
        for (int i = 0; size > i; i++)
            if (value == data[i]) { data[i] = data[--size]; return true; }
        return false;
    }

    public boolean removeAll(@NotNull final IntList list) {
        if (size == 0) return false;
        final int s = list.size();
        if (s == 0) return false;
        final int[] d = list.getData();
        boolean modified = false;
        int j, r, i;
        for (i = 0; i < s; i++) {
            r = d[i];
            for (j = 0; j < size; j++) {
                if (r != data[j]) continue;
                data[j] = data[--size];
                modified = true;
                break;
            }
        }
        return modified;
    }

    public void inc(final int index) {
        data[index]++;
    }

    public void dec(final int index) {
        data[index]--;
    }

    public void add(final int e) {
        ensureAdd(); data[size++] = e;
    }

    public void extendSet(final int index, final int e) {
        ensureSpace(index);
        data[index] = e;
    }

    public void unsafeAdd(final int e) {
        data[size++] = e;
    }

    public void justSet(final int index, final int e) {
        data[index] = e;
    }

    public void safeSet(final int index, final int e) {
        rangeCheck(index);
        data[index] = e;
    }

    public int set(final int index, final int e) {
        final int old = data[index];
        data[index] = e;
        return old;
    }

    public void safeSetSize(final int newSize) {
        if (newSize < 0) throw new IllegalStateException("Illegal size: " + newSize);
        extendSetSize(newSize);
    }

    public void setSize(final int newSize) {
        size = newSize;
    }

    public void ensureSpace(final int index) {
        if (index < size) return;
        extendSetSize(index + 1);
    }

    public void ensureSize(final int newSize) {
        if (newSize > size) extendSetSize(newSize);
    }

    public void clear() {
        size = 0;
    }

    public void reset() {
        data = new int[8];
        size = 0;
    }

    public void addAll(@NotNull final ImmutableIntList items) {
        final int s = items.size();
        for (int i = 0; i < s; i++) data[size++] = items.get(i);
    }

    public void safeAddAll(@NotNull final ImmutableIntList items) {
        if (items.isEmpty()) return;
        final int newSize = items.size() + size;
        grow(newSize);
        for (int i = 0; size < newSize; i++) { data[size] = items.get(i); size++; }
    }

    public void addAll(@NotNull final IntList items) {
        final int itemsSize = items.size();
        System.arraycopy(items.getData(), 0, data, size, itemsSize);
        size += itemsSize;
    }

    public void safeAddAll(@NotNull final IntList items) {
        final int itemsSize = items.size();
        if (itemsSize < 1) return;
        final int newSize = size + itemsSize;
        grow(newSize);
        System.arraycopy(items.getData(), 0, data, size, itemsSize);
        size = newSize;
    }

    public void addAll(@NotNull final int[] items) {
        final int itemsSize = items.length;
        System.arraycopy(items, 0, data, size, itemsSize);
        size += itemsSize;
    }

    public void safeAddAll(@NotNull final int[] items) {
        final int itemsSize = items.length;
        if (itemsSize == 0) return;
        final int newSize = size + itemsSize;
        grow(newSize);
        System.arraycopy(items, 0, data, size, itemsSize);
        size = newSize;
    }

    public void addAll(@NotNull final ImmutableIntSet items) {
        addAll(items, size + items.size());
    }

    public void addAll(@NotNull final ImmutableIntSet items, final int amount) {
        for (int i = 0; size < amount; i++) {
            long bitset = items.getWord(i);
            if (bitset == 0) continue;
            final int wordBits = i << 6;
            do {
                final long t = bitset & -bitset;
                data[size++] = wordBits + Long.bitCount(t - 1);
                bitset ^= t;
            } while (bitset != 0);
        }
    }

    public void setAll(@NotNull final ImmutableIntSet items) {
        size = 0;
        addAll(items, items.size());
    }

    public void safeSetAll(@NotNull final ImmutableIntSet items) {
        final int amount = items.size();
        size = 0;
        if (amount == 0) return;
        final int end = size + amount;
        grow(end);
        addAll(items, end);
    }

    public void safeAddAll(@NotNull final ImmutableIntSet items) {
        final int amount = items.size();
        if (amount == 0) return;
        final int end = size + amount;
        grow(end);
        addAll(items, end);
    }

    public void setAll(@NotNull final IntList items) {
        size = items.size;
        if (size == 0) return;
        if (size > data.length)
            data = items.data.clone(); else
            System.arraycopy(items.data, 0, data, 0, size);
    }

    public void setAll(@NotNull final int[] items) {
        size = items.length;
        if (size == 0) return;
        if (size > data.length)
            data = items.clone(); else
            System.arraycopy(items, 0, data, 0, size);
    }

    public void unsafeSetAll(final int[] items) {
        unsafeSetAll(items, items.length);
    }

    public void unsafeSetAll(final int[] items, final int newSize) {
        System.arraycopy(items, 0, data, 0, newSize);
        size = newSize;
    }

    public void ensureAdd() {
        if (size == data.length) setCapacity(data.length << 1);
    }

    public void ensureAdd(final int amount) {
        grow(size + amount);
    }

    public void grow(final int newCapacity) {
        if (newCapacity > data.length) setCapacity(Math.max(data.length << 1, newCapacity));
    }

    public void setData(@NotNull final int[] d) {
        data = d;
    }

    public void setData(final int capacity) {
        data = new int[capacity];
    }

    public void fix() {
        if (size < 0) size = 0;
        if (data == null) data = new int[8];
        if (size > data.length) size = data.length;
    }

    public void fullClear() {
        fullClearData();
        size = 0;
    }

    public void hardClear() {
        hardClearData();
        size = 0;
    }

    public void hardClearData() {
        for (int i = 0; i < size; i++) data[i] = 0;
    }

    public void fullClearData() {
        Arrays.fill(data, 0);
    }

    public int getOrExtendSet(final int index, final int defaultValue) {
        if (index < size) return data[index];
        extendSetSize(index + 1);
        data[index] = defaultValue;
        return defaultValue;
    }

    public void extendSetSize(final int newSize) {
        grow(newSize);
        size = newSize;
    }

    public void setCapacity(final int newCapacity) {
        data = ArrayUtils.unsafeCopyOf(data, newCapacity, size);
    }

    public void addClear(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        final int firstSize = first.size();
        if (firstSize == 0) return;
        grow(size + firstSize);
        doAddClear(first, second);
    }

    public void setClear(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        size = 0;
        final int firstSize = first.size();
        if (firstSize == 0) return;
        grow(firstSize);
        doAddClear(first, second);
    }

    public void addRetain(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        final int firstSize = first.size();
        final int secondSize = second.size();
        if (firstSize == 0 || secondSize == 0) return;
        grow(size + Math.min(firstSize, secondSize));
        doAddRetain(first, second);
    }

    public void setRetain(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        size = 0;
        final int firstSize = first.size();
        final int secondSize = second.size();
        if (firstSize == 0 || secondSize == 0) return;
        grow(Math.min(firstSize, secondSize));
        doAddRetain(first, second);
    }

    public void addClear(@NotNull final ImmutableIntList first, @NotNull final ImmutableIntSet second) {
        final int firstSize = first.size();
        if (firstSize == 0) return;
        grow(size + firstSize);
        doAddClear(first, second);
    }

    public void setClear(@NotNull final ImmutableIntList first, @NotNull final ImmutableIntSet second) {
        size = 0;
        final int firstSize = first.size();
        if (firstSize == 0) return;
        grow(firstSize);
        doAddClear(first, second);
    }



    protected void doAddClear(@NotNull final ImmutableIntList first, @NotNull final ImmutableIntSet second) {
        final int size = first.size();
        for (int i = 0; i < size; i++) {
            final int value = first.get(i);
            if (!second.contains(value)) unsafeAdd(value);
        }
    }

    protected void doAddRetain(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        final int commonWords = Math.min(first.capacity(), second.capacity());
        for (int i = 0; commonWords > i; i++) {
            long bitset = first.getWord(i) & second.getWord(i);
            if (bitset == 0) continue;
            final int wordBits = i << 6;
            do {
                final long t = bitset & -bitset;
                bitset ^= t;
                unsafeAdd(wordBits + Long.bitCount(t - 1));
            } while (bitset != 0);
        }
    }

    protected void doAddClear(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        final int commonWords = Math.min(first.capacity(), second.capacity());
        for (int i = 0; commonWords > i; i++) {
            long bitset = first.getWord(i) & ~second.getWord(i);
            if (bitset == 0) continue;
            final int wordBits = i << 6;
            do {
                final long t = bitset & -bitset;
                bitset ^= t;
                unsafeAdd(wordBits + Long.bitCount(t - 1));
            } while (bitset != 0);
        }
    }

    protected void outOfBounds(final int index) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    protected void rangeCheck(final int index) {
        if (index >= size) outOfBounds(index);
    }

    protected IntIterator createIterator() {
        return new IntListIterator();
    }



    @Override
    public int getOrDefault(final int index, final int defaultValue) {
        return index < size ? data[index] : defaultValue;
    }

    public int getOrZero(final int index) {
        return getOrDefault(index, 0);
    }

    @Override
    public int indexOf(final int value) {
        for (int i = 0; size > i; i++) if (value == data[i]) return i;
        return -1;
    }

    @Override
    public int first() {
        if (size < 1) outOfBounds(0);
        return data[0];
    }

    @Override
    public int last() {
        if (size < 1) outOfBounds(size - 1);
        return data[size - 1];
    }

    @Override
    public boolean contains(final int e) {
        for (final int v : data) if (e == v) return true;
        return false;
    }

    @Override
    public int safeGet(final int index) {
        rangeCheck(index);
        return data[index];
    }

    @Override
    public int get(final int index) {
        return data[index];
    }

    @Override
    public int capacity() {
        return data.length;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isNotEmpty() {
        return size > 0;
    }

    @Override
    public int[] toArray() {
        return ArrayUtils.unsafeCopyOf(data, size, size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int[] toArray(@NotNull final int[] a) {
        if (a.length < size) return toArray();
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = 0;
        return a;
    }

    @Override
    public boolean isBroken() {
        return size < 0 || size > data.length;
    }

    @Override
    public IntIterator intIterator() {
        iterator.reset();
        return iterator;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (size > 0) {
            sb.append(data[0]);
            for (int i = 1; i < size; i++) sb.append(", ").append(data[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IntList list = (IntList) o;
        if (size != list.size) return false;
        for (int i = 0; size > i; i++) if (data[i] != list.data[i]) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int e : data) hash = (127 * hash) + e;
        return hash;
    }



    @NotNull
    @Contract("null, _ -> new")
    public static IntList safeWrap(final int[] data, int size) {
        if (data == null) return new IntList(new int[Math.max(1, size)], size);
        final int l = data.length;
        if (size < 0) size = 0;
        if (l == 0) return new IntList(new int[Math.max(1, size)], size);
        if (l >= size) return new IntList(data, size);
        return new IntList(ArrayUtils.unsafeCopyOf(data, size, l), size);
    }

    @NotNull
    @Contract("null -> new")
    public static IntList safeWrap(final int[] data) {
        if (data == null) return new IntList();
        final int l = data.length;
        if (l == 0) return new IntList();
        return new IntList(data, l);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static IntList wrap(final int[] data, final int size) {
        return new IntList(data, size);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static IntList wrap(@NotNull final int[] data) {
        return new IntList(data, data.length);
    }



    protected class IntListIterator implements IntIterator {

        protected int cursor = 0;



        @Override
        public int nextInt() {
            return data[cursor++];
        }

        @Override
        public void reset() {
            cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }
    }
}
