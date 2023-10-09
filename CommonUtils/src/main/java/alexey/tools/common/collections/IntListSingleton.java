package alexey.tools.common.collections;

public class IntListSingleton implements ImmutableIntList {

    public int value;
    private final IntIterator iterator = new SingletonIntIterator();



    public IntListSingleton(final int value) {
        this.value = value;
    }



    @Override
    public int[] toArray() {
        return new int[] { value };
    }

    @Override
    public int[] toArray(int[] a) {
        if (a.length < 1) return toArray();
        a[0] = value;
        if (a.length > 1) a[1] = 0;
        return a;
    }

    @Override
    public int last() {
        return value;
    }

    @Override
    public int first() {
        return value;
    }

    @Override
    public int indexOf(int e) {
        return e == value ? 0 : -1;
    }

    @Override
    public int safeGet(int index) {
        return get(index);
    }

    @Override
    public int get(int index) {
        if (index == 0) return value;
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
    }

    @Override
    public int getOrDefault(int index, int defaultValue) {
        return index == 0 ? value : defaultValue;
    }

    @Override
    public int getOrZero(int index) {
        return index == 0 ? value : 0;
    }

    @Override
    public boolean isBroken() {
        return false;
    }

    @Override
    public boolean contains(int value) {
        return this.value == value;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isNotEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int capacity() {
        return 1;
    }

    @Override
    public IntIterator intIterator() {
        iterator.reset();
        return iterator;
    }

    @Override
    public String toString() {
        return "[" + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntListSingleton list = (IntListSingleton) o;
        return list.value == value;
    }

    @Override
    public int hashCode() {
        return value;
    }



    private class SingletonIntIterator implements IntIterator {

        private boolean hasNext = true;



        @Override
        public int nextInt() {
            hasNext = false;
            return value;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public void reset() {
            hasNext = true;
        }
    }
}
