package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ObjectContainer<V> implements Collection<V> {

    protected Entry[] entries;
    protected int size;
    protected final ObjectIterator iterator = new ObjectIterator();



    public ObjectContainer() {
        this(16);
    }

    public ObjectContainer(final int capacity) {
        entries = new Entry[capacity];
        size = 0;
    }



    @SuppressWarnings("unchecked")
    public Entry<V> addEntry(final V e) {
        Entry entry = entries[size];
        if (entry == null) {
            entry = new Entry<>(e, size);
            entries[size++] = entry;
            if (size == entries.length) setCapacity(size << 1);
        } else {
            entry.index = size++;
            entry.value = e;
        }
        return entry;
    }

    @SuppressWarnings("unchecked")
    public Entry<V> get(final int index) {
        return entries[index];
    }

    @SuppressWarnings("unchecked")
    public V replace(final int index, final V e) {
        final Entry entry = entries[index];
        final Object old = entry.value;
        entry.value = e;
        return (V) old;
    }

    public void set(final int index, final V e) {
        entries[index].value = e;
    }

    @SuppressWarnings("unchecked")
    public Entry<V> unsafeAdd(final V e) {
        Entry entry = entries[size];
        if (entry == null) {
            entry = new Entry<>(e, size);
            entries[size++] = entry;
        } else {
            entry.index = size++;
            entry.value = e;
        }
        return entry;
    }

    public void growIndex(final int index) {
        if (index < entries.length) return;
        setCapacity(Math.max(entries.length << 1, index + 2));
    }

    public void growCapacity(final int newCapacity) {
        if (newCapacity > entries.length) setCapacity(Math.max(entries.length << 1, newCapacity + 1));
    }

    public void setCapacity(final int newCapacity) {
        entries = Arrays.copyOf(entries, newCapacity);
    }

    public void justRemove(final int index) {
        final Entry top = entries[--size];
        final Entry target = entries[index];
        entries[size] = target;
        entries[index] = top;
        top.index = index;
        target.value = null;
        iterator.cursor--;
    }

    public void justRemoveCurrent() {
        final Entry top = entries[--size];
        final Entry target = entries[--iterator.cursor];
        entries[size] = target;
        entries[iterator.cursor] = top;
        top.index = iterator.cursor;
        target.value = null;
    }

    @SuppressWarnings("unchecked")
    public Entry<V> remove(final int index) {
        final Entry top = entries[--size];
        final Entry target = entries[index];
        entries[size] = target;
        entries[index] = top;
        top.index = index;
        target.value = null;
        iterator.cursor--;
        return target;
    }

    public Entry<V> safeRemove(final int index) {
        return index < size ? remove(index) : null;
    }



    @Override
    public boolean remove(final Object e) {
        if (e == null) {
            for (int i = 0; i < size; i++) if (entries[i].get() == null) { justRemove(i); return true; } } else
            for (int i = 0; i < size; i++) if (e.equals(entries[i].get())) { justRemove(i); return true; }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull final Collection<?> c) {
        for (final Object e : c) if (!contains(e)) return false;
        return true;
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends V> c) {
        if (c.isEmpty()) return false;
        growCapacity(c.size() + size);
        for (final V o: c) unsafeAdd(o);
        return true;
    }

    @Override
    public boolean removeAll(@NotNull final Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size; i++) if (c.contains(entries[i].get())) { justRemove(i); modified = true; }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull final Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size; i++) if (!c.contains(entries[i].get())) { justRemove(i); modified = true; }
        return modified;
    }

    @Override
    public void clear() {
        while (size > 0) entries[--size] = null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(final Object e) {
        if (e == null) {
            for (int i = 0; size > i; i++) if (entries[i].get() == null) return true; } else {
            for (int i = 0; size > i; i++) if (e.equals(entries[i].get())) return true; }
        return false;
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        iterator.cursor = 0;
        return iterator;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        final Object[] result = new Object[size];
        fill(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        if (a.length < size) {
            final Object[] result = (Object[]) Array.newInstance(a.getClass(), size);
            fill(result);
            return (T[]) result;
        }
        fill(a);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public boolean add(final V v) {
        Entry entry = entries[size];
        if (entry == null) {
            entry = new Entry<>(v, size);
            entries[size++] = entry;
            if (size == entries.length) setCapacity(size << 1);
        } else {
            entry.index = size++;
            entry.value = v;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (size > 0) {
            sb.append(entries[0].get());
            for (int i = 1; i < size; i++)
                sb.append(", ").append(entries[i].get());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectContainer<?> list = (ObjectContainer<?>) o;
        if (size != list.size()) return false;
        for (int i = 0; size > i; i++) if (!entries[i].get().equals(list.entries[i].get())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        if (size == 0) return 0;
        int hash = entries[0].get().hashCode();
        for (int i = 1; size > i; i++)
            hash = 127 * hash + entries[i].get().hashCode();
        return hash;
    }



    private void fill(final Object[] destination) {
        for (int i = 0; i < size; i++) destination[i] = entries[i].get();
    }



    public class Entry<T> {

        protected int index;
        protected T value;



        protected Entry(final T v, final int i) {
            value = v;
            index = i;
        }



        public void remove() {
            justRemove(index);
        }

        public void set(final T t) {
            value = t;
        }

        public T replace(final T t) {
            final T old = value;
            value = t;
            return old;
        }

        public T get() {
            return value;
        }
    }

    protected class ObjectIterator implements Iterator<V> {

        protected int cursor = 0;



        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public V next() {
            return (V) entries[cursor++].value;
        }

        @Override
        public void remove() {
            justRemoveCurrent();
        }
    }
}
