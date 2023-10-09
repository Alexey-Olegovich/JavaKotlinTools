package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;

public class IntSetSingleton implements ImmutableIntSet {

    private final int value;
    private final IntIterator iterator = new SingletonIntIterator();



    public IntSetSingleton(final int value) {
        this.value = value;
    }



    @Override
    public int capacity() {
        return value >>> 6 + 1;
    }

    @Override
    public long getWord(int index) {
        final int last = value >>> 6;
        if (index == last) return 1L << value;
        if (index < last && index > -1) return 0;
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }

    @Override
    public boolean isNotEmpty() {
        return true;
    }

    @Override
    public boolean contains(int value) {
        return this.value == value;
    }

    @Override
    public boolean unsafeContains(int value) {
        return this.value == value;
    }

    @Override
    public int length() {
        return value + 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getNextExistingValue(int fromValue) {
        return fromValue <= value ? value : -1;
    }

    @Override
    public int getNextMissingValue(int fromValue) {
        return fromValue == value ? fromValue + 1 : fromValue;
    }

    @Override
    public boolean containsAny(@NotNull IntSet other) {
        return other.contains(value);
    }

    @Override
    public boolean contains(@NotNull IntSet other) {
        return other.contains(value) && other.size() == 1;
    }

    @Override
    public int size() {
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
    public int hashCode() {
        final long bitset = 1L << value;
        return (int) (bitset ^ (bitset >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IntSetSingleton other = (IntSetSingleton) obj;
        return other.value == value;
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
