package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class ObjectStorage<T> extends CompactObjectStorage<T> {

    protected int size;



    protected ObjectStorage(Object[] data, int size) {
        super(data);
        this.size = size;
    }

    // NEW CONSTRUCTORS ------------------------------------------------------------------------------------------------

    public ObjectStorage() {
        this(16);
    }

    public ObjectStorage(final int capacity) {
        this(new Object[capacity], 0);
    }

    public ObjectStorage(final @NotNull Object[] other, final int off, final int len) {
        super(other, off, len);
        size = len;
    }

    public ObjectStorage(final @NotNull Object[] data) {
        this(data.clone(), data.length);
    }

    // NEW METHODS -----------------------------------------------------------------------------------------------------

    public void setSize(final int newSize) {
        size = newSize;
    }

    public boolean hasSpace() {
        return size < data.length;
    }

    public void ensureSpace(final int index) {
        if (index < size) return;
        extendSetSize(index + 1);
    }

    public void ensureSize(final int newSize) {
        if (newSize > size) extendSetSize(newSize);
    }

    public void extendSetSize(final int newSize) {
        size = newSize;
        growCapacity(size);
    }

    public void ensureAdd() {
        if (size == data.length) setCapacity(size << 1);
    }

    public void ensureAdd(final int amount) {
        growCapacity(size + amount);
    }

    public void unsafeAdd(final T e) {
        data[size++] = e;
    }

    public void unsafeAddAll(final @NotNull Iterable<? extends T> items) {
        for (T item : items) data[size++] = item;
    }

    public void unsafeAddAll(final @NotNull Collection<? extends T> items) {
        final int itemsSize = items.size();
        System.arraycopy(items.toArray(), 0, data, size, itemsSize);
        size += itemsSize;
    }

    @SuppressWarnings("unchecked")
    public void sortData(Comparator comparator) {
        Arrays.sort(data, 0, size, comparator);
    }

    public void unsafeAddNull() {
        size++;
    }

    public T safeRemoveAt(int index) {
        return index < size ? remove(index) : null;
    }

    public T safeRemoveLast() {
        if (size < 1) outOfBounds(size);
        return removeLast();
    }

    public T safeSet(int index, T e) {
        rangeCheck(index);
        return super.set(index, e);
    }

    public T safeGet(int index) {
        rangeCheck(index);
        return get(index);
    }

    public int lastIndexOf(final Object e) {
        if (e == null) {
            for (int i = size - 1; i > -1; i--) if (data[i] == null)   return i; } else {
            for (int i = size - 1; i > -1; i--) if (e.equals(data[i])) return i; }
        return -1;
    }

    public void add(final int index, final T element) {
        final Object e = data[index];
        ensureAdd();
        data[index] = element;
        data[size++] = e;
    }

    public boolean addAll(int index, final @NotNull Collection<? extends T> collection) {
        if (index == size) return addAll(collection);
        if (collection.isEmpty() || index > size) return false;
        final Object[] collectionArray = collection.toArray();
        final int collectionSize = collectionArray.length;
        final int newSize = collectionSize + size;
        growCapacity(newSize);
        final int delta = size - index;
        if (delta < collectionSize)
            System.arraycopy(data, index, data, index + collectionSize, delta); else
            System.arraycopy(data, index, data, size, collectionSize);
        System.arraycopy(collectionArray, 0, data, index, collectionSize);
        size = newSize;
        return true;
    }

    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size; i++) if (c.contains(data[i])) { justRemove(i); modified = true; }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size; i++) if (!c.contains(data[i])) { justRemove(i); modified = true; }
        return modified;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEach(final Consumer<? super T> action) {
        for (int i = 0; i < size; i++) action.accept((T) data[i]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T set(final int index, final T e) {
        final Object old = data[index];
        data[index] = e;
        return (T) old;
    }

    @Override
    public void clear() {
        while (size > 0) data[--size] = null;
    }

    @Override
    public boolean add(final T t) {
        ensureAdd(); data[size++] = t; return true;
    }

    @Override
    public boolean remove(final Object e) {
        if (e == null) {
            for (int i = 0; i < size; i++) if (data[i] == null)   { justRemove(i); return true; } } else
            for (int i = 0; i < size; i++) if (e.equals(data[i])) { justRemove(i); return true; }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T remove(final int index) {
        Object e = data[index];
        justRemove(index);
        return (T) e;
    }

    @Override
    public boolean addAll(final @NotNull Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        final int cSize = c.size();
        final int newSize = cSize + size;
        growCapacity(newSize);
        System.arraycopy(c.toArray(), 0, data, size, cSize);
        size = newSize;
        return true;
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E> E[] toArray(final @NotNull E[] a) {
        if (a.length < size) return (E[]) Arrays.copyOf(data, size, a.getClass());
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public int indexOf(final Object e) {
        if (e == null) {
            for (int i = 0; size > i; i++) if (data[i] == null)   return i; } else {
            for (int i = 0; size > i; i++) if (e.equals(data[i])) return i; }
        return -1;
    }

    @Override
    public boolean contains(final Object e) {
        if (e == null) {
            for (int i = 0; size > i; i++) if (data[i] == null)   return true; } else {
            for (int i = 0; size > i; i++) if (e.equals(data[i])) return true; }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(final int index) {
        return (T) data[index];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void extendSet(final int index, final T e) {
        ensureSpace(index);
        data[index] = e;
    }

    @Override
    public void justRemove(final int index) {
        data[index] = data[--size];
        data[size] = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeLast() {
        final Object e = data[--size];
        data[size] = null;
        return (T) e;
    }

    @Override
    public void addNull() {
        ensureAdd(); size++;
    }

    @Override
    public void addAll(final @NotNull CompactObjectStorage<? extends T> items) {
        final int itemsSize = items.size();
        if (itemsSize < 1) return;
        final int newSize = size + itemsSize;
        growCapacity(newSize);
        System.arraycopy(items.getData(), 0, data, size, itemsSize);
        size = newSize;
    }

    @Override
    public void addAll(final @NotNull T[] items) {
        final int itemsSize = items.length;
        if (itemsSize == 0) return;
        final int newSize = size + itemsSize;
        growCapacity(newSize);
        System.arraycopy(items, 0, data, size, itemsSize);
        size = newSize;
    }

    @Override
    public void clearData() {
        ArrayUtils.unsafeFill(data, 0, size, null);
    }

    public void hardClear() {
        data = new Object[2];
        size = 0;
    }

    @Override
    public boolean removeReference(final Object e) {
        for (int i = 0; i < size; i++) {
            if (data[i] == e) {
                justRemove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getOrNull(final int index) {
        return index < size ? (T) data[index] : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getOrDefault(int index, T value) {
        return index < size ? (T) data[index] : value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T last() {
        return (T) data[size - 1];
    }

    @Override
    public int indexOfNull() {
        for (int i = 0; size > i; i++) if (data[i] == null) return i;
        return -1;
    }

    @Override
    public boolean equalReference(final int index, Object e) {
        return index < size && data[index] == e;
    }

    @Override
    public boolean isNull(int index) {
        return index >= size || data[index] == null;
    }

    @Override
    public boolean containsReference(final Object e) {
        for (int i = 0; size > i; i++) if (data[i] == e) return true;
        return false;
    }

    @Override
    public boolean isNotEmpty() {
        return size > 0;
    }

    @Override
    public void setAll(@NotNull T[] items) {
        final int newSize = items.length;
        if (newSize == 0) { clear(); return; }
        if (newSize == size) { System.arraycopy(items, 0, data, 0, size); return; }
        if (newSize < size) {
            ArrayUtils.unsafeFill(data, newSize, size, null);
            System.arraycopy(items, 0, data, 0, newSize);
        } else data = items.clone();
        size = newSize;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
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
        ObjectStorage<?> list = (ObjectStorage<?>) o;
        if (size != list.size()) return false;
        for (int i = 0; size > i; i++) if (!data[i].equals(list.data[i])) return false;
        return true;
    }

    @Override
    public int hashCode() {
        if (size == 0) return 0;
        int hash = data[0].hashCode();
        for (int i = 1; size > i; i++)
            hash = 127 * hash + data[i].hashCode();
        return hash;
    }

    @Override
    protected ObjectStorageIterator createIterator() {
        return new ObjectStorageIterator();
    }



    protected void outOfBounds(int index) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    protected void rangeCheck(int index) {
        if (index >= size) outOfBounds(index);
    }



    @NotNull
    public static <T> ObjectStorage<T> wrap(final T[] data) {
        return wrap(data, data.length);
    }

    @NotNull
    public static <T> ObjectStorage<T> wrap(final T[] data, final int size) {
        return new ObjectStorage<>(data, size);
    }



    protected class ObjectStorageIterator extends CompactObjectStorageIterator {

        @Override
        public boolean hasNext() {
            return cursor < size;
        }
    }
}
