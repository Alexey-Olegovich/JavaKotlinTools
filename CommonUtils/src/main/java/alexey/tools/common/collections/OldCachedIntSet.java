package alexey.tools.common.collections;

@Deprecated
public class OldCachedIntSet extends OptimizedIntSet {

    transient protected IntList cache;



    public OldCachedIntSet() {
        super();
    }

    public OldCachedIntSet(final int capacity) {
        super(capacity);
    }

    public OldCachedIntSet(final IntSet other) {
        super(other);
    }



    @Override
    protected IntIterator createIterator() {
        cache = new IntList(0);
        cache.size = -1;
        return cache.iterator;
    }

    @Override
    protected void setSize(final int size) {
        this.size = size;
        cache.size = -1;
    }

    @Override
    public IntIterator intIterator() {
        if (cache.size == -1) {
            cache.size = 0;
            cache.grow(size);
            cache.addAll(this, size);
        }
        iterator.reset();
        return iterator;
    }
}
