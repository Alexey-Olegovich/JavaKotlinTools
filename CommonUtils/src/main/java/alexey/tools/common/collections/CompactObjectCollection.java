package alexey.tools.common.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.Iterator;

public class CompactObjectCollection<T> extends CompactObjectStorage<T> {

    transient final protected CompactObjectStorageIterator iterator = createIterator();



    protected CompactObjectCollection(final @NotNull Object[] data) {
        super(data);
    }



    public CompactObjectCollection() {
        super();
    }

    public CompactObjectCollection(final int size) {
        super(size);
    }

    public CompactObjectCollection(final @NotNull Object[] other, final int off, final int len) {
        super(other, off, len);
    }



    @Override
    public Iterator<T> iterator() {
        iterator.cursor = 0;
        return iterator;
    }



    @NotNull
    public static <T> CompactObjectCollection<T> wrap(final T[] data) {
        return new CompactObjectCollection<>(data);
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> CompactObjectCollection<T> newInstance(@NotNull final T[] data) {
        return new CompactObjectCollection<>(data.clone());
    }
}
