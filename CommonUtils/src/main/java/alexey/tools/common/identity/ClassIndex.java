package alexey.tools.common.identity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ClassIndex {

    private int index = 0;

    private final ClassValue<TypeProperties<?>> map = new ClassValue<TypeProperties<?>>() {
        @NotNull
        @Contract("_ -> new")
        @Override
        protected TypeProperties<?> computeValue(Class<?> type) {
            return new TypeProperties<>(type, index++);
        }
    };



    public int obtain(final Class<?> type) {
        return map.get(type).id;
    }

    public int size() {
        return index;
    }

    @SuppressWarnings("unchecked")
    public <T> TypeProperties<T> obtainProperties(final Class<T> type) {
        return (TypeProperties<T>) map.get(type);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public IndexKey obtain(@NotNull final Object[] objects) {
        final int length = objects.length;
        final boolean[] checkArray = new boolean[index + length + 1];
        int min = Integer.MAX_VALUE, max = 0;
        for (int i = 0; i < length; i++) {
            final int value = obtain(objects[i].getClass());
            if (checkArray[value]) throw new IllegalArgumentException("Duplicate object types are not allowed");
            checkArray[value] = true;
            min = Math.min(value, min);
            max = Math.max(value, max);
        }
        return new IndexKey(checkArray, min, max, length);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public IndexKey obtain(@NotNull final Class<?>[] classes) {
        final int length = classes.length;
        final boolean[] checkArray = new boolean[index + length + 1];
        int min = Integer.MAX_VALUE, max = 0;
        for (int i = 0; i < length; i++) {
            final int value = obtain(classes[i]);
            if (checkArray[value]) throw new IllegalArgumentException("Duplicate object types are not allowed");
            checkArray[value] = true;
            min = Math.min(value, min);
            max = Math.max(value, max);
        }
        return new IndexKey(checkArray, min, max, length);
    }

    public <E extends Enum<E>> IndexKey obtain(@NotNull final E enumValue) {
        return new IndexKey(new int[] { obtain(enumValue.getClass()), enumValue.ordinal() });
    }
}
