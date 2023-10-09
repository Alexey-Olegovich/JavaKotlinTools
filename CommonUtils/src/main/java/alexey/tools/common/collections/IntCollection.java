package alexey.tools.common.collections;

public interface IntCollection extends IntIterable {
    default boolean contains(int value) { return false; }
    default boolean isEmpty() { return true; }
    default boolean isNotEmpty() { return false; }
    default int size() { return 0; }
    default int capacity() { return 0; }



    IntCollection EMPTY = new IntCollection() {};

    static IntCollection emptyCollection() { return EMPTY; }
}
