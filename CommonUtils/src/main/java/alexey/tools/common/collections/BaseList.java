package alexey.tools.common.collections;

import java.util.List;
import java.util.ListIterator;

public class BaseList<T> extends BaseCollection<T> implements List<T> {

    protected BaseList(final Object[] data) {
        super(data);
    }



    public BaseList() {
        super();
    }

    public BaseList(final int capacity) {
        super(capacity);
    }



    public int length() {
        return size();
    }



    @Override
    protected AbstractListIterator createIterator() {
        return new AbstractListIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        iterator.cursor = 0;
        iterator.size = size();
        return iterator;
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        iterator.cursor = index;
        iterator.size = size();
        return iterator;
    }



    protected class AbstractListIterator extends AbstractStorageIterator {

        public int backward = -1;



        public AbstractListIterator() {
            super(0);
        }



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
}
