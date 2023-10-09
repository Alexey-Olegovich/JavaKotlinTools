package alexey.tools.common.context;

import java.util.function.Consumer;

public interface ImmutableVariable {
    default float toFloat() { return 0F; }
    default byte toByte() { return 0; }
    default int toInt() { return 0; }
    default double toDouble() { return 0.0; }
    default short toShort() { return 0; }
    default long toLong() { return 0L; }
    default boolean toBoolean() { return false; }

    default Object getValue() { return ""; }
    default byte type() { return STRING; }
    default boolean isValid() { return !isInvalid(); }
    default boolean isInvalid() { return Float.isNaN(toFloat()); }
    default Variable copy() { return Variable.NULL; }
    default void addListener(final Consumer<ImmutableVariable> listener) {}



    byte BOOLEAN = 0, INTEGER = 1, DECIMAL = 2, STRING = 3;
    ImmutableVariable NULL = new ImmutableVariable() {};
}
