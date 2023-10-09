package alexey.tools.common.level;

import alexey.tools.common.collections.ImmutableIntSet;
import alexey.tools.common.identity.TypeProperties;

public interface Entity {
    default <T> T get(Class<T> componentType) {
        T r = getOrNull(componentType);
        if (r == null) throw new NullPointerException();
        return r;
    }

    default <T> T get(TypeProperties<T> typeProperties) {
        T r = getOrNull(typeProperties);
        if (r == null) throw new NullPointerException();
        return r;
    }

    default <T> T getOrNull(Class<T> componentType) {
        return null;
    }

    default <T> T getOrNull(TypeProperties<T> typeProperties) {
        return null;
    }

    default <T> T remove(Class<T> componentType) {
        return null;
    }

    default <T> T remove(TypeProperties<T> typeProperties) {
        return null;
    }

    default <T> T set(T component) {
        return null;
    }

    default <T> T set(TypeProperties<T> typeProperties, T component) {
        return null;
    }

    default boolean has(Class<?> componentType) {
        return false;
    }

    default boolean has(TypeProperties<?> typeProperties) {
        return false;
    }

    default void delete() {

    }

    default int getId() {
        return -1;
    }

    default ImmutableIntSet getComponentTypes() {
        return ImmutableIntSet.EMPTY;
    }

    default World getWorld() {
        return null;
    }
}
