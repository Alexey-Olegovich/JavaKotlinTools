package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;

public class IntSet implements ImmutableIntSet {

    protected long[] words;
    transient protected final IntIterator iterator = createIterator();



    public IntSet() {
        this(1);
    }

    public IntSet(final int capacity) {
        words = new long[capacity];
    }

    public IntSet(@NotNull final IntSet copyFrom) {
        words = copyFrom.words.clone();
    }



    public void setWord(final int index, final long word) {
        words[index] = word;
    }

    public void add(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        words[word] |= 1L << value;
    }

    public boolean put(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        final long previousValue = words[word];
        final long newValue = previousValue | (1L << value);
        words[word] = newValue;
        return previousValue != newValue;
    }

    public void unsafeAdd(final int value) {
        words[value >>> 6] |= 1L << value;
    }

    public void flip(final int value) {
        final int word = value >>> 6;
        ensureSpace(word);
        words[word] ^= 1L << value;
    }

    public void ensureCapacity(final int capacity) {
        ensureSpace(capacity >>> 6);
    }

    public boolean remove(final int value) {
        final int word = value >>> 6;
        if (word >= words.length) return false;
        final long previousValue = words[word];
        final long newValue = previousValue & ~(1L << value);
        words[word] = newValue;
        return previousValue != newValue;
    }

    public void clear(final int value) {
        final int word = value >>> 6;
        if (word >= words.length) return;
        words[word] &= ~(1L << value);
    }

    public void unsafeClear(final int value) {
        words[value >>> 6] &= ~(1L << value);
    }

    public void clear() {
        Arrays.fill(words, 0L);
    }

    public void retain(@NotNull final IntSet other) {
        final int commonWords = Math.min(words.length, other.words.length);
        int i = 0;
        for (; commonWords > i; i++) words[i] &= other.words[i];
        if (commonWords != other.words.length) return;
        for (final int s = words.length; s > i; i++) words[i] = 0L;
    }

    public void clear(@NotNull final IntSet values) {
        final int commonWords = Math.min(words.length, values.words.length);
        for (int i = 0; commonWords > i; i++) words[i] &= ~values.words[i];
    }

    public void add(@NotNull final IntSet other) {
        final int otherCapacity = other.words.length;
        final int commonWords = Math.min(words.length, otherCapacity);
        for (int i = 0; i < commonWords; i++) words[i] |= other.words[i];
        if (commonWords == otherCapacity) return;
        setCapacity(otherCapacity);
        System.arraycopy(other.words, commonWords, words, commonWords, otherCapacity - commonWords);
    }

    public void add(@NotNull final ImmutableIntSet other) {
        final int otherCapacity = other.capacity();
        final int commonWords = Math.min(words.length, otherCapacity);
        int i = 0;
        for (; i < commonWords; i++) words[i] |= other.getWord(i);
        if (commonWords == otherCapacity) return;
        setCapacity(otherCapacity);
        for (; i < otherCapacity; i++) words[i] = other.getWord(i);
    }

    public void set(@NotNull final ImmutableIntSet other) {
        final int otherCapacity = other.capacity();
        if (words.length < otherCapacity) setCapacity(otherCapacity);
        int i = 0;
        for (; i < otherCapacity; i++) words[i] = other.getWord(i);
        final int ownerCapacity = words.length;
        for (; i < ownerCapacity; i++) words[i] = 0L;
    }

    public void set(@NotNull final IntSet other) {
        final long[] otherWords = other.words;
        int i = otherWords.length;
        if (words.length < i) setCapacity(i);
        System.arraycopy(otherWords, 0, words, 0, i);
        final int ownerCapacity = words.length;
        for (; i < ownerCapacity; i++) words[i] = 0L;
    }

    public void add(@NotNull final ImmutableIntList other) {
        final IntIterator i = other.intIterator();
        while (i.hasNext()) add(i.nextInt());
    }

    public void removeOrAdd(@NotNull final IntSet other) {
        final int otherCapacity = other.words.length;
        final int commonWords = Math.min(words.length, otherCapacity);
        for (int i = 0; commonWords > i; i++) words[i] ^= other.words[i];
        if (commonWords == otherCapacity) return;
        setCapacity(otherCapacity);
        System.arraycopy(other.words, commonWords, words, commonWords, otherCapacity - commonWords);
    }

    public void addRetain(@NotNull final ImmutableIntSet first, @NotNull final ImmutableIntSet second) {
        final int commonWords = Math.min(first.capacity(), second.capacity());
        for (int i = 0; commonWords > i; i++) words[i] = first.getWord(i) & second.getWord(i);
    }



    @Override
    public boolean unsafeContains(final int value) {
        return (words[value >>> 6] & (1L << value)) != 0L;
    }

    @Override
    public int length() {
        final long[] bits = this.words;
        for (int word = bits.length - 1; word >= 0; --word) {
            final long bitsAtWord = bits[word];
            if (bitsAtWord != 0) return (word << 6) + 64 - Long.numberOfLeadingZeros(bitsAtWord);
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        for (final long bit : words) if (bit != 0L) return false;
        return true;
    }

    @Override
    public boolean isNotEmpty() {
        for (final long bit : words) if (bit != 0L) return true;
        return false;
    }

    @Override
    public int capacity() {
        return words.length;
    }

    @Override
    public int getNextExistingValue(final int fromValue) {
        final int word = fromValue >>> 6;
        if (word >= words.length) return -1;
        long bitmap = words[word] >>> fromValue;
        if (bitmap != 0) return fromValue + Long.numberOfTrailingZeros(bitmap);
        for (int i = 1 + word; i < words.length; i++) {
            bitmap = words[i];
            if (bitmap != 0) return i * 64 + Long.numberOfTrailingZeros(bitmap);
        }
        return -1;
    }

    @Override
    public int getNextMissingValue(final int fromValue) {
        final int word = fromValue >>> 6;
        if (word >= words.length) return Math.min(fromValue, words.length << 6);
        long bitmap = ~(words[word] >>> fromValue);
        if (bitmap != 0) return fromValue + Long.numberOfTrailingZeros(bitmap);
        for (int i = 1 + word; i < words.length; i++) {
            bitmap = ~words[i];
            if (bitmap != 0) return i * 64 + Long.numberOfTrailingZeros(bitmap);
        }
        return Math.min(fromValue, words.length << 6);
    }

    @Override
    public boolean containsAny(@NotNull final IntSet other) {
        final long[] bits = this.words;
        final long[] otherBits = other.words;
        for (int i = 0, s = Math.min(bits.length, otherBits.length); s > i; i++)
            if ((bits[i] & otherBits[i]) != 0) return true;
        return false;
    }

    @Override
    public long getWord(final int index) {
        return words[index];
    }

    @Override
    public boolean contains(@NotNull final IntSet other) {
        final long[] bits = this.words;
        final long[] otherBits = other.words;
        final int otherBitsLength = otherBits.length;
        final int bitsLength = bits.length;
        for (int i = bitsLength; i < otherBitsLength; i++)
            if (otherBits[i] != 0) return false;
        for (int i = 0, s = Math.min(bitsLength, otherBitsLength); s > i; i++)
            if ((bits[i] & otherBits[i]) != otherBits[i]) return false;
        return true;
    }

    @Override
    public int size() {
        int count = 0;
        for (final long word : words) count += Long.bitCount(word);
        return count;
    }

    @Override
    public boolean contains(final int value) {
        final int word = value >>> 6;
        return word < words.length &&
                (words[word] & (1L << value)) != 0L;
    }

    @Override
    public int hashCode() {
        final int word = length() >>> 6;
        int hash = 0;
        for (int i = 0; word >= i; i++) {
            hash = 127 * hash + (int) (words[i] ^ (words[i] >>> 32));
        }
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final IntSet other = (IntSet) obj;
        final long[] otherBits = other.words;
        final int commonWords = Math.min(words.length, otherBits.length);
        for (int i = 0; commonWords > i; i++)
            if (words[i] != otherBits[i]) return false;
        if (words.length == otherBits.length) return true;
        return length() == other.length();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final IntIterator i = intIterator();
        if (i.hasNext()) {
            sb.append(i.nextInt());
            while (i.hasNext())
                sb.append(", ").append(i.nextInt());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public IntIterator intIterator() {
        iterator.reset();
        return iterator;
    }



    protected IntIterator createIterator() {
        return new IntSetIterator();
    }

    protected void ensureSpace(final int index) {
        if (index < words.length) return;
        setCapacity(index + 1);
    }

    protected void setCapacity(final int capacity) {
        final long[] newBits = new long[capacity];
        System.arraycopy(words, 0, newBits, 0, words.length);
        words = newBits;
    }



    private class IntSetIterator implements IntIterator {

        private int i;
        private int size;
        private int cursor;
        private long bitset;
        private int wordBits;



        @Override
        public boolean hasNext() {
            if (bitset != 0) return true;
            if (cursor >= size) return false;
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
            cursor++;
            return wordBits + Long.bitCount(t - 1);
        }

        @Override
        public void reset() {
            size = size();
            i = 0;
            bitset = words[0];
            wordBits = 0;
            cursor = 0;
        }
    }
}
