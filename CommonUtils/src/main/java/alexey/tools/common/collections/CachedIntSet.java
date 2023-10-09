package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public class CachedIntSet extends IntSet {

    protected int[] cache;
    protected boolean needUpdate = false;
    protected int size = 0;
    protected final IntIterator cacheIterator = new CacheIterator();



    public CachedIntSet() {
        super();
        cache = new int[8];
    }

    public CachedIntSet(final int capacity) {
        super(capacity);
        cache = new int[8];
    }

    public CachedIntSet(final int capacity, final int cacheCapacity) {
        super(capacity);
        cache = new int[cacheCapacity];
    }

    public CachedIntSet(final IntSet other) {
        super(other);
        size = other.size();
        cache = new int[Math.min(8, size)];
        needUpdate = true;
    }



    protected void cache(final int value) {
        if (needUpdate) {
            size++;
            return;
        }
        if (cache.length == size) cache = ArrayUtils.unsafeCopyOf(cache, size << 1, size);
        unsafeCache(value);
    }

    protected void unsafeCache(final int value) {
        cache[size++] = value;
    }

    protected void setAndCount(final int index, final long value) {
        size += Long.bitCount(value);
        words[index] = value;
    }



    @Override
    public void add(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        final long previousValue = words[word];
        final long newValue = previousValue | (1L << value);
        if (previousValue == newValue) return;
        cache(value);
        words[word] = newValue;
    }

    @Override
    public boolean put(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        final long previousValue = words[word];
        final long newValue = previousValue | (1L << value);
        if (previousValue == newValue) return false;
        cache(value);
        words[word] = newValue;
        return true;
    }

    @Override
    public void unsafeAdd(final int value) {
        super.unsafeAdd(value);
        unsafeCache(value);
    }

    @Override
    public void flip(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        final long bits = words[word];
        final long shift = 1L << value;
        if ((bits & shift) == 0) {
            cache(value);
        } else {
            needUpdate = true;
            size--;
        }
        words[word] = bits ^ shift;
    }

    @Override
    public boolean remove(final int value) {
        final int word = value >>> 6;
        if (word >= words.length) return false;
        final long previousValue = words[word];
        final long newValue = previousValue & ~(1L << value);
        if (previousValue == newValue) return false;
        needUpdate = true;
        size--;
        words[word] = newValue;
        return true;
    }

    @Override
    public void clear(final int value) {
        final int word = value >>> 6;
        if (word >= words.length) return;
        final long previousValue = words[word];
        final long newValue = previousValue & ~(1L << value);
        if (previousValue == newValue) return;
        needUpdate = true;
        size--;
        words[word] = newValue;
    }

    @Override
    public void unsafeClear(final int value) {
        super.unsafeClear(value);
        needUpdate = true;
        size--;
    }

    @Override
    public void clear() {
        super.clear();
        size = 0;
        needUpdate = false;
    }

    @Override
    public void retain(@NotNull final IntSet other) {
        final int commonWords = Math.min(words.length, other.words.length);
        size = 0;
        needUpdate = true;
        int i = 0;
        for (; commonWords > i; i++) setAndCount(i, words[i] & other.words[i]);
        if (commonWords != other.words.length) return;
        for (final int s = words.length; s > i; i++) words[i] = 0L;
    }

    @Override
    public void clear(@NotNull final IntSet values) {
        final int commonWords = Math.min(words.length, values.words.length);
        size = 0;
        needUpdate = true;
        int i = 0;
        for (; commonWords > i; i++) setAndCount(i, words[i] & ~values.words[i]);
        for (; words.length > i; i++) size += Long.bitCount(words[i]);
    }

    @Override
    public void add(@NotNull final IntSet other) {
        final int otherCapacity = other.words.length;
        final int commonWords = Math.min(words.length, otherCapacity);
        size = 0;
        needUpdate = true;
        int i = 0;
        for (; i < commonWords; i++) setAndCount(i, words[i] | other.words[i]);
        if (commonWords == otherCapacity) return;
        setCapacity(otherCapacity);
        for (; i < otherCapacity; i++) setAndCount(i, other.words[i]);
    }

    @Override
    public void add(@NotNull final ImmutableIntSet other) {
        final int otherCapacity = other.capacity();
        final int commonWords = Math.min(words.length, otherCapacity);
        size = 0;
        needUpdate = true;
        int i = 0;
        for (; i < commonWords; i++) setAndCount(i, words[i] | other.getWord(i));
        if (commonWords == otherCapacity) return;
        setCapacity(otherCapacity);
        for (; i < otherCapacity; i++) setAndCount(i, other.getWord(i));
    }

    @Override
    public void set(@NotNull final ImmutableIntSet other) {
        final int otherCapacity = other.capacity();
        if (words.length < otherCapacity) setCapacity(otherCapacity);
        size = 0;
        needUpdate = true;
        int i = 0;
        for (; i < otherCapacity; i++) setAndCount(i, other.getWord(i));
        final int ownerCapacity = words.length;
        for (; i < ownerCapacity; i++) words[i] = 0L;
    }

    @Override
    public void set(@NotNull final IntSet other) {
        final long[] otherWords = other.words;
        final int otherCapacity = otherWords.length;
        if (words.length < otherCapacity) setCapacity(otherCapacity);
        size = 0;
        needUpdate = true;
        int i = 0;
        for (; i < otherCapacity; i++) setAndCount(i, otherWords[i]);
        final int ownerCapacity = words.length;
        for (; i < ownerCapacity; i++) words[i] = 0L;
    }

    @Override
    public void removeOrAdd(@NotNull final IntSet other) {
        final int otherCapacity = other.words.length;
        final int commonWords = Math.min(words.length, otherCapacity);
        size = 0;
        needUpdate = true;
        int i = 0;
        for (; commonWords > i; i++) setAndCount(i, words[i] ^ other.words[i]);
        if (commonWords == otherCapacity) return;
        setCapacity(otherCapacity);
        for (; i < otherCapacity; i++) setAndCount(i, other.words[i]);
    }

    @Override
    public void addRetain(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        final int commonWords = Math.min(first.capacity(), second.capacity());
        size = 0;
        needUpdate = true;
        for (int i = 0; commonWords > i; i++) setAndCount(i, first.getWord(i) & second.getWord(i));
    }



    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isNotEmpty() {
        return size != 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    protected IntIterator createIterator() {
        return new CommonIterator();
    }

    @Override
    public IntIterator intIterator() {
        IntIterator i = needUpdate ? cacheIterator : iterator;
        i.reset();
        return i;
    }



    private class CommonIterator implements IntIterator {

        private int cursor = 0;



        @Override
        public int nextInt() {
            return cache[cursor++];
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public void remove() {
            final int value = cache[--cursor];
            cache[cursor] = cache[--size];
            CachedIntSet.super.unsafeClear(value);
        }

        @Override
        public void reset() {
            cursor = 0;
        }
    }

    private class CacheIterator implements IntIterator {

        private int i;
        private int cursor;
        private long bitset;
        private int wordBits;



        @Override
        public boolean hasNext() {
            if (bitset != 0) return true;
            if (cursor >= size) {
                needUpdate = false;
                return false;
            }
            do {
                bitset = words[++i];
            } while (bitset == 0);
            wordBits = i << 6;
            return true;
        }

        @Override
        public int nextInt() {
            final long t = bitset & -bitset;
            bitset ^= t;
            final int result = wordBits + Long.bitCount(t - 1);
            cache[cursor++] = result;
            return result;
        }

        @Override
        public void reset() {
            i = 0;
            bitset = words[0];
            wordBits = 0;
            cursor = 0;
            if (size > cache.length) cache = new int[Math.max(size, cache.length << 1)];
        }

        @Override
        public void remove() {
            size--;
            CachedIntSet.super.unsafeClear(cache[--cursor]);
        }
    }
}
