package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.util.*;

public class ObjectCollection<T> extends ObjectStorage<T> {

    transient final protected ObjectStorageIterator iterator = createIterator();



    protected ObjectCollection(Object[] data, int size) {
        super(data, size);
    }



    public ObjectCollection() {
        super();
    }

    public ObjectCollection(int capacity) {
        super(capacity);
    }

    public ObjectCollection(final @NotNull Object[] other, final int off, final int len) {
        super(other, off, len);
    }

    public ObjectCollection(final @NotNull Object[] data) {
        super(data);
    }



    @NotNull
    @Override
    public Iterator<T> iterator() {
        iterator.cursor = 0;
        return iterator;
    }



    @NotNull
    public static <T> ObjectCollection<T> wrap(final T[] data) {
        return wrap(data, data.length);
    }

    @NotNull
    public static <T> ObjectCollection<T> wrap(final T[] data, final int size) {
        return new ObjectCollection<>(data, size);
    }
}
