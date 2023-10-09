package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class OptimizedIntSet extends IntSet {

    protected int size = 0;



    public OptimizedIntSet() {
        super();
    }

    public OptimizedIntSet(final int capacity) {
        super(capacity);
    }

    public OptimizedIntSet(final IntSet other) {
        super(other);
        size = other.size();
    }



    @Override
    public void add(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        final long previousValue = words[word];
        final long newValue = previousValue | (1L << value);
        if (previousValue == newValue) return;
        setSize(size + 1);
        words[word] = newValue;
    }

    @Override
    public boolean put(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        final long previousValue = words[word];
        final long newValue = previousValue | (1L << value);
        if (previousValue == newValue) return false;
        setSize(size + 1);
        words[word] = newValue;
        return true;
    }

    @Override
    public void unsafeAdd(final int value) {
        words[value >>> 6] |= 1L << value;
        setSize(size + 1);
    }

    @Override
    public void flip(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        final long bits = words[word];
        final long shift = 1L << value;
        setSize(size + ((bits & shift) == 0 ? 1 : -1));
        words[word] = bits ^ shift;
    }

    @Override
    public boolean remove(final int value) {
        final int word = value >>> 6;
        if (word >= words.length) return false;
        final long previousValue = words[word];
        final long newValue = previousValue & ~(1L << value);
        if (previousValue == newValue) return false;
        setSize(size - 1);
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
        setSize(size - 1);
        words[word] = newValue;
    }

    @Override
    public void unsafeClear(final int value) {
        words[value >>> 6] &= ~(1L << value);
        setSize(size - 1);
    }

    @Override
    public void clear() {
        Arrays.fill(words, 0L);
        setSize(0);
    }

    @Override
    public void retain(@NotNull final IntSet other) {
        final int commonWords = Math.min(words.length, other.words.length);
        setSize(0);
        int i = 0;
        for (; commonWords > i; i++) setAndCount(i, words[i] & other.words[i]);
        if (commonWords != other.words.length) return;
        for (final int s = words.length; s > i; i++) words[i] = 0L;
    }

    @Override
    public void clear(@NotNull final IntSet values) {
        final int commonWords = Math.min(words.length, values.words.length);
        setSize(0);
        int i = 0;
        for (; commonWords > i; i++) setAndCount(i, words[i] & ~values.words[i]);
        for (; words.length > i; i++) size += Long.bitCount(words[i]);
    }

    @Override
    public void add(@NotNull final IntSet other) {
        final int otherCapacity = other.words.length;
        final int commonWords = Math.min(words.length, otherCapacity);
        setSize(0);
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
        setSize(0);
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
        setSize(0);
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
        setSize(0);
        int i = 0;
        for (; i < otherCapacity; i++) setAndCount(i, otherWords[i]);
        final int ownerCapacity = words.length;
        for (; i < ownerCapacity; i++) words[i] = 0L;
    }

    @Override
    public void removeOrAdd(@NotNull final IntSet other) {
        final int otherCapacity = other.words.length;
        final int commonWords = Math.min(words.length, otherCapacity);
        setSize(0);
        int i = 0;
        for (; commonWords > i; i++) setAndCount(i, words[i] ^ other.words[i]);
        if (commonWords == otherCapacity) return;
        setCapacity(otherCapacity);
        for (; i < otherCapacity; i++) setAndCount(i, other.words[i]);
    }

    @Override
    public void addRetain(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        final int commonWords = Math.min(first.capacity(), second.capacity());
        setSize(0);
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
        return new OptimizedIntSetIterator();
    }



    protected void setSize(final int size) {
        this.size = size;
    }

    protected void setAndCount(final int index, final long value) {
        size += Long.bitCount(value);
        words[index] = value;
    }



    private class OptimizedIntSetIterator implements IntIterator {

        private int i;
        private int index;
        private long bitset;
        private int wordBits;



        @Override
        public boolean hasNext() {
            if (bitset != 0) return true;
            if (index >= size) return false;
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
            index++;
            return wordBits + Long.bitCount(t - 1);
        }

        @Override
        public void reset() {
            i = 0;
            bitset = words[0];
            wordBits = 0;
            index = 0;
        }
    }
}
