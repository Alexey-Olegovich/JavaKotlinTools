package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.util.Iterator;

public class BaseCollection<T> extends BaseStorage<T> {

    transient final protected AbstractStorageIterator iterator = createIterator();



    protected BaseCollection(Object[] data) {
        super(data);
    }



    public BaseCollection() {
        super();
    }

    public BaseCollection(int capacity) {
        super(capacity);
    }



    @NotNull
    @Override
    public Iterator<T> iterator() {
        iterator.cursor = 0;
        iterator.size = size();
        return iterator;
    }

    @Override
    protected AbstractStorageIterator createIterator() {
        return new AbstractStorageIterator(0);
    }
}
