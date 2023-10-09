package alexey.tools.common.context;

public interface Variable extends ImmutableVariable {
    default void set(final Number number) {}
    default void set(final int number) {}
    default void set(final short number) {}
    default void set(final long number) {}
    default void set(final byte number) {}
    default void set(final float number) {}
    default void set(final double number) {}
    default void set(final String string) {}
    default void set(final boolean b) {}
    default void set(final Object value) {}
    default void invalidate() { set(Float.NaN); }



    default float decreaseToZero(final float amount) {
        final float value = toFloat();
        if (value <= 0F) return value;
        if (value < amount)
            set(0F); else
            set(value - amount);
        return value;
    }

    default int decreaseToZero(final int amount) {
        final int value = toInt();
        if (value <= 0) return value;
        if (value < amount)
            set(0); else
            set(value - amount);
        return value;
    }

    default void add(final int value) {
        set(toInt() + value);
    }

    default void add(final float value) {
        set(toFloat() + value);
    }

    default int addAndGet() {
        return addAndGet(1);
    }

    default int getAndAdd() {
        return getAndAdd(1);
    }

    default int addAndGet(final int v) {
        final int value = toInt() + v;
        set(value);
        return value;
    }

    default int getAndAdd(final int v) {
        final int value = toInt();
        set(value + v);
        return value;
    }

    default float addAndGet(final float v) {
        final float value = toFloat() + v;
        set(value);
        return value;
    }

    default float getAndAdd(final float v) {
        final float value = toFloat();
        set(value + v);
        return value;
    }



    Variable NULL = new Variable() {};
}
