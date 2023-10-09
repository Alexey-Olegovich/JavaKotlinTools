package alexey.tools.common.collections;

public interface ImmutableIntList extends IntCollection {

    default int[] toArray() { return new int[0]; }
    default int[] toArray(int[] a) { return a; }
    default int last() { throw new IndexOutOfBoundsException("0"); }
    default int first() { throw new IndexOutOfBoundsException("0"); }
    default int indexOf(int e) { return -1; }
    default int safeGet(int index) { throw new IndexOutOfBoundsException(Integer.toString(index)); }
    default int get(int index) { throw new IndexOutOfBoundsException(Integer.toString(index)); }
    default int getOrDefault(int index, int defaultValue) { return defaultValue; }
    default int getOrZero(int index) { return 0; }
    default boolean isBroken() { return false; }



    ImmutableIntList EMPTY = new ImmutableIntList() {};

    static ImmutableIntList emptyList() { return EMPTY; }
}
