package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;

public interface ImmutableIntSet extends IntCollection {

    default boolean unsafeContains(int value) { return false; }
    default int length() { return 0; }
    default int getNextExistingValue(int fromValue) { return -1; }
    default int getNextMissingValue(int fromValue) { return -1; }
    default boolean containsAny(@NotNull IntSet other) { return false; }
    default boolean contains(@NotNull IntSet other) { return false; }
    default long getWord(int index) { throw new IndexOutOfBoundsException(Integer.toString(index)); }
    @NotNull
    default ImmutableIntList toList() {
        final int size = size();
        if (size == 0) return ImmutableIntList.EMPTY;
        final IntList result = new IntList(size);
        result.addAll(this, size);
        return result;
    }



    ImmutableIntSet EMPTY = new ImmutableIntSet() {};

    static ImmutableIntSet emptySet() { return EMPTY; }
}
