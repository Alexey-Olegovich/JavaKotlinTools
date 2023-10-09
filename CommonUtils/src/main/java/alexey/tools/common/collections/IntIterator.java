package alexey.tools.common.collections;

import java.util.Iterator;

public interface IntIterator extends Iterator<Integer> {
    int nextInt();

    default void reset() {}

    @Override
    default Integer next() { return nextInt(); }



    IntIterator EMPTY = new IntIterator() {

        @Override
        public int nextInt() {
            throw new IndexOutOfBoundsException("0");
        }

        @Override
        public boolean hasNext() {
            return false;
        }
    };
}
