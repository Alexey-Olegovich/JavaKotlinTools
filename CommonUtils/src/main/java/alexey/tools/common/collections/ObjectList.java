package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.util.*;

public class ObjectList<T> extends ObjectCollection<T> implements List<T> {



    protected ObjectList(final Object[] data, final int size) {
        super(data, size);
    }



    public ObjectList(final @NotNull Object[] data) {
        super(data);
    }

    public ObjectList(final @NotNull Object[] other, final int off, final int len) {
        super(other, off, len);
    }

    public ObjectList() {
        super();
    }

    public ObjectList(final int capacity) {
        super(capacity);
    }

    public ObjectList(final @NotNull Collection<T> data) {
        super(data.size());
        unsafeAddAll(data);
    }



    public int length() {
        return size;
    }

    public ObjectList<T> copy() {
        final Object[] copy = new Object[size];
        System.arraycopy(data, 0, copy, 0, size);
        return new ObjectList<>(copy, size);
    }



    @Override
    protected ObjectListIterator createIterator() {
        return new ObjectListIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        iterator.cursor = 0;
        return iterator;
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        iterator.cursor = index;
        return iterator;
    }



    protected class ObjectListIterator extends ObjectStorageIterator {

        public int backward = -1;



        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T previous() {
            backward = --cursor;
            return (T) data[backward];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            justRemove(backward == cursor ? cursor : --cursor);
        }

        @Override
        public void set(final T t) {
            data[backward == cursor ? cursor : cursor - 1] = t;
        }

        @Override
        public void add(final T t) {
            if (backward != cursor) {
                if (cursor == size) {
                    cursor++;
                    justAdd(t);
                    return;
                }
                final Object temp = data[cursor];
                data[cursor++] = t;
                justAdd(temp);
                return;
            }
            final Object temp1 = data[cursor];
            data[cursor] = t;
            cursor++;
            if (cursor == size) {
                justAdd(temp1);
                return;
            }
            final Object temp2 = data[cursor];
            data[cursor] = temp1;
            justAdd(temp2);
        }

        protected void justAdd(final Object e) {
            ensureAdd(); data[size++] = e;
        }
    }



    @NotNull
    public static <T> ObjectList<T> wrap(final T[] data) {
        return wrap(data, data.length);
    }

    @NotNull
    public static <T> ObjectList<T> wrap(final T[] data, final int size) {
        return new ObjectList<>(data, size);
    }

    @NotNull
    public static <T> ObjectList<T> concatenate(@NotNull final Collection<T> first, @NotNull final Collection<T> second) {
        ObjectList<T> result = new ObjectList<>(first.size() + second.size());
        result.unsafeAddAll(first);
        result.unsafeAddAll(second);
        return result;
    }
}
