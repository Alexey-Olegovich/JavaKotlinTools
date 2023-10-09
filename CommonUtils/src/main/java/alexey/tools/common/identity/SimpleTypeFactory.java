package alexey.tools.common.identity;

import java.util.IdentityHashMap;

public class SimpleTypeFactory implements ImmutableTypeFactory {

    final protected IdentityHashMap<Class<?>, TypeProperties<?>> registered = new IdentityHashMap<>();


    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeProperties<T> get(Class<T> clazz) {
        return (TypeProperties<T>) registered.get(clazz);
    }

    public <T> TypeProperties<T> obtain(Class<T> clazz) {
        TypeProperties<T> d = get(clazz);
        if (d == null) {
            d = new TypeProperties<>(clazz, registered.size());
            registered.put(clazz, d);
        }
        return d;
    }

    @Override
    public int size() {
        return registered.size();
    }

    public void clear() {
        registered.clear();
    }
}
