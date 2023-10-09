package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;

public class IndexedObjectCollection<T extends IndexedObject> extends ObjectCollection<T> {



    protected IndexedObjectCollection(final Object[] data, final int size) {
        super(data, size);
    }



    public IndexedObjectCollection() {
        super();
    }

    public IndexedObjectCollection(final int capacity) {
        super(capacity);
    }

    public IndexedObjectCollection(final @NotNull Object[] other, final int off, final int len) {
        super(other, off, len);
    }

    public IndexedObjectCollection(final @NotNull Object[] data) {
        super(data.clone(), data.length);
    }



    public boolean safeRemoveReference(@NotNull final T e) {
        final int removeIndex = e.getIndex();
        if (removeIndex >= size || removeIndex < 0) return false;
        IndexedObject last = (IndexedObject) data[--size];
        last.setIndex(removeIndex);
        data[removeIndex] = last;
        data[size] = null;
        e.setIndex(-1);
        return true;
    }

    public void removeReference(@NotNull final T e) {
        final IndexedObject last = (IndexedObject) data[--size];
        final int removeIndex = e.getIndex();
        last.setIndex(removeIndex);
        data[removeIndex] = last;
        data[size] = null;
    }



    @Override
    public boolean add(@NotNull final T e) {
        ensureAdd();
        e.setIndex(size);
        data[size++] = e;
        return true;
    }

    @Override
    public void unsafeAdd(@NotNull final T e) {
        e.setIndex(size);
        data[size++] = e;
    }

    @Override
    protected ObjectStorageIterator createIterator() {
        return new IndexedObjectCollectionIterator();
    }



    protected class IndexedObjectCollectionIterator extends ObjectStorageIterator {

        @Override
        public void remove() {
            final IndexedObject last = (IndexedObject) data[--size];
            last.setIndex(--cursor);
            data[cursor] = last;
            data[size] = null;
        }
    }



    @NotNull
    public static <T extends IndexedObject> IndexedObjectCollection<T> wrap(final T[] data) {
        return wrap(data, data.length);
    }

    @NotNull
    public static <T extends IndexedObject> IndexedObjectCollection<T> wrap(final T[] data, final int size) {
        return new IndexedObjectCollection<>(data, size);
    }
}
