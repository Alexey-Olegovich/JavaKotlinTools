package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CompactObjectStorage<T> implements Collection<T> {

    protected Object[] data;



    protected CompactObjectStorage(final @NotNull Object[] data) {
        this.data = data;
    }



    public CompactObjectStorage() {
        this(0);
    }

    public CompactObjectStorage(final int size) {
        this(new Object[size]);
    }

    public CompactObjectStorage(final @NotNull Object[] other, final int off, final int len) {
        this(new Object[len]);
        System.arraycopy(other, off, data, 0, len);
    }



    public void makeFirst(int index) {
        if (index == 0) return;
        final Object e = data[index];
        System.arraycopy(data, 0, data, 1, index);
        data[0] = e;
    }

    public void justSet(int index, T e) {
        data[index] = e;
    }

    public void extendSet(int index, T e) {
        ensureSpace(index);
        data[index] = e;
    }

    public void ensureSpace(final int index) {
        if (index < data.length) return;
        setCapacity(index + 1);
    }

    public void growIndex(final int index) {
        if (index < data.length) return;
        setCapacity(Math.max(data.length << 1, index + 1));
    }

    public void growCapacity(final int newCapacity) {
        if (newCapacity > data.length) setCapacity(Math.max(data.length << 1, newCapacity));
    }

    public void justSetNull(final int index) {
        data[index] = null;
    }

    public void ensureSize(final int size) {
        if (size > data.length) setCapacity(size);
    }

    public void setCapacity(int newCapacity) {
        data = Arrays.copyOf(data, newCapacity);
    }

    @SuppressWarnings("unchecked")
    public T remove(final int index) {
        final Object e = data[index];
        justRemove(index);
        return (T) e;
    }

    public void justRemove(final int index) {
        data = ArrayUtils.unsafeClearAt(data, index);
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        int last = data.length - 1;
        Object e = data[last];
        setCapacity(last);
        return (T) e;
    }

    public void addNull() {
        setCapacity(data.length + 1);
    }

    public T setNull(final int index) {
        return set(index, null);
    }

    @SuppressWarnings("unchecked")
    public T set(final int index, final T e) {
        final Object old = data[index];
        data[index] = e;
        return (T) old;
    }

    public void addAll(@NotNull CompactObjectStorage<? extends T> items) {
        int itemsSize = items.size();
        if (itemsSize < 1) return;
        int size = data.length;
        setCapacity(size + itemsSize);
        System.arraycopy(items.getData(), 0, data, size, itemsSize);
    }

    public void addAll(@NotNull T[] items) {
        int itemsSize = items.length;
        if (itemsSize == 0) return;
        int size = data.length;
        setCapacity(size + itemsSize);
        System.arraycopy(items, 0, data, size, itemsSize);
    }

    public void clearData() {
        Arrays.fill(data, null);
    }

    public void setAll(@NotNull T[] items) {
        if (data.length != items.length)
            data = items.clone(); else
            System.arraycopy(items, 0, data, 0, items.length);
    }

    public Object[] getData() {
        return data;
    }

    public void setData(final @NotNull T[] items) {
        data = items;
    }

    @SuppressWarnings("unchecked")
    public @NotNull List<T> subList(final int fromIndex, final int toIndex) {
        return (List<T>) ObjectList.wrap(Arrays.copyOfRange(data, fromIndex, toIndex));
    }

    public boolean removeReference(Object e) {
        Object[] copy = ArrayUtils.minus(data, e);
        if (copy == data) return false;
        data = copy;
        return true;
    }

    @SuppressWarnings("unchecked")
    public T getOrNull(int index) {
        return index < data.length ? (T) data[index] : null;
    }

    @SuppressWarnings("unchecked")
    public T getOrDefault(int index, T value) {
        return index < data.length ? (T) data[index] : value;
    }

    public boolean equalReference(final int index, Object e) {
        return index < data.length && data[index] == e;
    }

    public boolean isNull(final int index) {
        return index >= data.length || data[index] == null;
    }

    @SuppressWarnings("unchecked")
    public T last() {
        return (T) data[data.length - 1];
    }

    @SuppressWarnings("unchecked")
    public T first() {
        return (T) data[0];
    }

    public int indexOf(Object e) {
        int size = data.length;
        if (e == null) {
            for (int i = 0; size > i; i++) if (data[i] == null) return i; } else {
            for (int i = 0; size > i; i++) if (e.equals(data[i])) return i; }
        return -1;
    }

    public int indexOfNull() {
        int size = data.length;
        for (int i = 0; size > i; i++) if (data[i] == null) return i;
        return -1;
    }

    public boolean containsReference(Object e) {
        for (Object d : data) if (e == d) return true;
        return false;
    }

    public boolean inBounds(final int index) {
        return index < data.length && index > -1;
    }

    public boolean outBounds(final int index) {
        return index < 0 || index >= data.length;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) data[index];
    }

    public int capacity() {
        return data.length;
    }

    public boolean isNotEmpty() {
        return data.length > 0;
    }



    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object e : c) if (!contains(e)) return false;
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = false;
        int size = data.length;
        for (int i = 0; i < size; i++) if (c.contains(data[i])) {
            data[i] = data[--size];
            modified = true;
        }
        data = Arrays.copyOf(data, size);
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean modified = false;
        int size = data.length;
        for (int i = 0; i < size; i++) if (!c.contains(data[i])) {
            data[i] = data[--size];
            modified = true;
        }
        data = Arrays.copyOf(data, size);
        return modified;
    }

    @Override
    public void clear() {
        data = new Object[0];
    }

    @Override
    public boolean add(T t) {
        data = ArrayUtils.plus(data, t);
        return true;
    }

    @Override
    public boolean remove(Object e) {
        int index = indexOf(e);
        if (index == -1) return false;
        justRemove(index);
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        final int size = data.length;
        final int cSize = c.size();
        setCapacity(cSize + size);
        System.arraycopy(c.toArray(), 0, data, size, cSize);
        return true;
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return data.clone();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E> E[] toArray(@NotNull E[] a) {
        int size = data.length;
        if (a.length < size) return (E[]) Arrays.copyOf(data, size, a.getClass());
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEach(final Consumer<? super T> action) {
        for (Object e : data) action.accept((T) e);
    }

    @Override
    public boolean contains(Object e) {
        if (e == null) {
            for (Object d : data) if (d == null) return true; } else {
            for (Object d : data) if (e.equals(d)) return true; }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return data.length == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return createIterator();
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int size = data.length;
        if (size > 0) {
            sb.append(data[0]);
            for (int i = 1; i < size; i++)
                sb.append(", ").append(data[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompactObjectStorage<?> list = (CompactObjectStorage<?>) o;
        int size = data.length;
        if (size != list.data.length) return false;
        for (int i = 0; size > i; i++) if (!data[i].equals(list.data[i])) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Object e : data) hash = (127 * hash) + e.hashCode();
        return hash;
    }



    protected CompactObjectStorageIterator createIterator() {
        return new CompactObjectStorageIterator();
    }



    @NotNull
    public static <T> CompactObjectStorage<T> wrap(final T[] data) {
        return new CompactObjectStorage<>(data);
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> CompactObjectStorage<T> newInstance(@NotNull final T[] data) {
        return new CompactObjectStorage<>(data.clone());
    }



    protected class CompactObjectStorageIterator implements ListIterator<T> {

        public int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < data.length;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            return (T) data[cursor++];
        }

        @Override
        public void remove() {
            justRemove(--cursor);
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException("hasPrevious");
        }

        @Override
        public T previous() {
            throw new UnsupportedOperationException("previous");
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException("nextIndex");
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("previousIndex");
        }

        @Override
        public void set(T t) {
            throw new UnsupportedOperationException("set");
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException("add");
        }
    }
}
