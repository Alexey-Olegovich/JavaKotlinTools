package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public class BaseStorage<T> extends CompactObjectStorage<T> {

    protected BaseStorage(Object[] data) {
        super(data);
    }

    // NEW CONSTRUCTORS ------------------------------------------------------------------------------------------------

    public BaseStorage() {
        this(16);
    }

    public BaseStorage(final int capacity) {
        this(new Object[capacity]);
    }

    // NEW METHODS -----------------------------------------------------------------------------------------------------

    public void setSize(final int newSize) {
        throw new UnsupportedOperationException("setSize");
    }

    public int getAndAdd() {
        final int size = size();
        setSize(size + 1);
        return size;
    }

    public boolean hasSpace() {
        return size() < data.length;
    }

    public void ensureSpace(final int index) {
        if (index < size()) return;
        extendSetSize(index + 1);
    }

    public void ensureSize(final int newSize) {
        if (newSize > size()) extendSetSize(newSize);
    }

    public void extendSetSize(final int newSize) {
        setSize(newSize);
        growCapacity(newSize);
    }

    public void ensureAdd() {
        if (size() == data.length) setCapacity(data.length << 1);
    }

    public void ensureAdd(final int amount) {
        growCapacity(size() + amount);
    }

    public void unsafeAdd(final T e) {
        data[getAndAdd()] = e;
    }

    public void unsafeAddAll(final @NotNull Iterable<? extends T> items) {
        for (T item : items) data[getAndAdd()] = item;
    }

    public void unsafeAddAll(final @NotNull Collection<? extends T> items) {
        final int itemsSize = items.size();
        final int size = size();
        System.arraycopy(items.toArray(), 0, data, size, itemsSize);
        setSize(itemsSize + size);
    }

    @SuppressWarnings("unchecked")
    public void sortData(Comparator comparator) {
        Arrays.sort(data, 0, size(), comparator);
    }

    public void unsafeAddNull() {
        setSize(size() + 1);
    }

    public T safeRemoveAt(int index) {
        final int size = size();
        return index < size ? remove(index, size) : null;
    }

    public T safeRemoveLast() {
        final int size = size();
        if (size < 1) outOfBounds(size - 1, size);
        return removeLast(size);
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
            for (int i = size() - 1; i > -1; i--) if (data[i] == null)   return i; } else {
            for (int i = size() - 1; i > -1; i--) if (e.equals(data[i])) return i; }
        return -1;
    }

    public void add(final int index, final T element) {
        final Object e = data[index];
        int size = size();
        ensureAdd0(size);
        data[index] = element;
        data[size++] = e;
        setSize(size);
    }

    public boolean addAll(int index, final @NotNull Collection<? extends T> collection) {
        final int size = size();
        if (index == size) return addAll(collection, size);
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
        setSize(newSize);
        return true;
    }

    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = false;
        final int size = size();
        for (int i = 0; i < size; i++) if (c.contains(data[i])) { justRemove(i, size); modified = true; }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean modified = false;
        final int size = size();
        for (int i = 0; i < size; i++) if (!c.contains(data[i])) { justRemove(i, size); modified = true; }
        return modified;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEach(final Consumer<? super T> action) {
        final int size = size();
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
        int size = size();
        while (size > 0) data[--size] = null;
        setSize(0);
    }

    @Override
    public boolean add(final T t) {
        int size = size();
        ensureAdd0(size);
        data[size++] = t;
        setSize(size);
        return true;
    }

    @Override
    public boolean remove(final Object e) {
        final int size = size();
        if (e == null) {
            for (int i = 0; i < size; i++) if (data[i] == null)   { justRemove(i, size); return true; } } else
            for (int i = 0; i < size; i++) if (e.equals(data[i])) { justRemove(i, size); return true; }
        return false;
    }

    @Override
    public T remove(final int index) {
        return remove(index, size());
    }

    @Override
    public boolean addAll(final @NotNull Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        final int cSize = c.size();
        final int size = size();
        final int newSize = cSize + size;
        growCapacity(newSize);
        System.arraycopy(c.toArray(), 0, data, size, cSize);
        setSize(newSize);
        return true;
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return Arrays.copyOf(data, size());
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E> E[] toArray(final @NotNull E[] a) {
        final int size = size();
        if (a.length < size) return (E[]) Arrays.copyOf(data, size, a.getClass());
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public int indexOf(final Object e) {
        final int size = size();
        if (e == null) {
            for (int i = 0; size > i; i++) if (data[i] == null)   return i; } else {
            for (int i = 0; size > i; i++) if (e.equals(data[i])) return i; }
        return -1;
    }

    @Override
    public boolean contains(final Object e) {
        final int size = size();
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
        return size() == 0;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("size");
    }

    @Override
    public void extendSet(final int index, final T e) {
        ensureSpace(index);
        data[index] = e;
    }

    @Override
    public void justRemove(final int index) {
        justRemove(index, size());
    }

    @Override
    public T removeLast() {
        return removeLast(size());
    }

    @Override
    public void addNull() {
        final int size = size();
        ensureAdd0(size);
        setSize(size + 1);
    }

    @Override
    public void addAll(final @NotNull CompactObjectStorage<? extends T> items) {
        final int itemsSize = items.size();
        if (itemsSize < 1) return;
        final int size = size();
        final int newSize = size + itemsSize;
        growCapacity(newSize);
        System.arraycopy(items.getData(), 0, data, size, itemsSize);
        setSize(newSize);
    }

    @Override
    public void addAll(final @NotNull T[] items) {
        final int itemsSize = items.length;
        if (itemsSize == 0) return;
        final int size = size();
        final int newSize = size + itemsSize;
        growCapacity(newSize);
        System.arraycopy(items, 0, data, size, itemsSize);
        setSize(newSize);
    }

    @Override
    public void clearData() {
        ArrayUtils.unsafeFill(data, 0, size(), null);
    }

    public void hardClear() {
        data = new Object[2];
        setSize(0);
    }

    @Override
    public boolean removeReference(final Object e) {
        final int size = size();
        for (int i = 0; i < size; i++) {
            if (data[i] == e) {
                justRemove(i, size);
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getOrNull(final int index) {
        return index < size() ? (T) data[index] : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getOrDefault(int index, T value) {
        return index < size() ? (T) data[index] : value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T last() {
        return (T) data[size() - 1];
    }

    @Override
    public int indexOfNull() {
        final int size = size();
        for (int i = 0; size > i; i++) if (data[i] == null) return i;
        return -1;
    }

    @Override
    public boolean equalReference(final int index, Object e) {
        return index < size() && data[index] == e;
    }

    @Override
    public boolean isNull(int index) {
        return index >= size() || data[index] == null;
    }

    @Override
    public boolean containsReference(final Object e) {
        final int size = size();
        for (int i = 0; size > i; i++) if (data[i] == e) return true;
        return false;
    }

    @Override
    public boolean isNotEmpty() {
        return size() > 0;
    }

    @Override
    public void setAll(@NotNull T[] items) {
        final int newSize = items.length;
        if (newSize == 0) { clear(); return; }
        final int size = size();
        if (newSize == size) { System.arraycopy(items, 0, data, 0, size); return; }
        if (newSize < size) {
            ArrayUtils.unsafeFill(data, newSize, size, null);
            System.arraycopy(items, 0, data, 0, newSize);
        } else data = items.clone();
        setSize(newSize);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        final int size = size();
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
        BaseStorage<?> list = (BaseStorage<?>) o;
        final int size = size();
        if (size != list.size()) return false;
        for (int i = 0; size > i; i++) if (!data[i].equals(list.data[i])) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int size = size();
        if (size == 0) return 0;
        int hash = data[0].hashCode();
        for (int i = 1; size > i; i++)
            hash = 127 * hash + data[i].hashCode();
        return hash;
    }

    @Override
    protected AbstractStorageIterator createIterator() {
        return new AbstractStorageIterator(size());
    }



    protected void outOfBounds(final int index, final int size) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    protected void rangeCheck(final int index) {
        final int size = size();
        if (index >= size) outOfBounds(index, size);
    }

    protected void justRemove(final int index, int size) {
        data[index] = data[--size];
        data[size] = null;
        setSize(size);
    }

    protected void ensureAdd0(final int size) {
        if (size == data.length) setCapacity(data.length << 1);
    }

    @SuppressWarnings("unchecked")
    protected T removeLast(int size) {
        final Object e = data[--size];
        data[size] = null;
        setSize(size);
        return (T) e;
    }

    @SuppressWarnings("unchecked")
    protected T remove(final int index, final int size) {
        Object e = data[index];
        justRemove(index, size);
        return (T) e;
    }

    protected boolean addAll(final @NotNull Collection<? extends T> c, final int size) {
        if (c.isEmpty()) return false;
        final int cSize = c.size();
        final int newSize = cSize + size;
        growCapacity(newSize);
        System.arraycopy(c.toArray(), 0, data, size, cSize);
        setSize(newSize);
        return true;
    }



    protected class AbstractStorageIterator extends CompactObjectStorageIterator {

        public int size;



        public AbstractStorageIterator(final int size) {
            this.size = size;
        }

        public AbstractStorageIterator() {
            this(0);
        }



        @Override
        public boolean hasNext() {
            return cursor < size;
        }
    }
}
