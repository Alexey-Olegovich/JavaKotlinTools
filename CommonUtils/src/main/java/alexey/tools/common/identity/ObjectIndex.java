package alexey.tools.common.identity;

import org.jetbrains.annotations.NotNull;
import java.util.Iterator;

public class ObjectIndex implements Iterable<Object> {

    private final Object[] objects;
    private final int shift;
    private int size = 0;



    public ObjectIndex(final int limit) {
        objects = new Object[1 << limit];
        shift = 32 - limit;
    }



    public int add(final Object obj) {
        final int length = objects.length;
        if (size == length) return -1;
        final int hash = System.identityHashCode(obj);
        int index = hash >> shift;
        while (objects[index] != null) {
            index++;
            if (index == length) index = 0;
        }
        objects[index] = obj;
        size++;
        return index;
    }

    public int obtain(final Object obj) {
        final int length = objects.length;
        final int hash = System.identityHashCode(obj);
        int index = hash >> shift;
        final int start = index;
        Object entry;
        do {
            entry = objects[index];
            if (entry == obj) return index;
            if (entry == null) {
                objects[index] = obj;
                size++;
                return index;
            }
            if (++index == length) index = 0;
        } while (index != start);
        return -1;
    }

    public int get(final Object obj) {
        final int length = objects.length;
        final int hash = System.identityHashCode(obj);
        int index = hash >> shift;
        final int start = index;
        while (objects[index] != obj) {
            index++;
            if (index == length) {
                index = 0;
            } else {
                if (index == start) return -1;
            }
        }
        return index;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return objects.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }



    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return new ObjectIterator();
    }



    private class ObjectIterator implements Iterator<Object> {

        private int cursor = 0;
        private int counter = 0;



        @Override
        public boolean hasNext() {
            return counter < objects.length;
        }

        @Override
        public Object next() {
            Object o;
            do {
                o = objects[cursor++];
            } while (o == null);
            counter++;
            return o;
        }
    }
}
