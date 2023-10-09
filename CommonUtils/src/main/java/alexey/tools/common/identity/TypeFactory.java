package alexey.tools.common.identity;

public class TypeFactory extends SimpleTypeFactory {

    final protected IdFactory idFactory = new IdFactory();



    @Override
    public <T> TypeProperties<T> obtain(Class<T> clazz) {
        TypeProperties<T> d = get(clazz);
        if (d == null) {
            d = new TypeProperties<>(clazz, idFactory.obtain());
            registered.put(clazz, d);
        }
        return d;
    }

    @Override
    public void clear() {
        idFactory.clear();
        registered.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> TypeProperties<T> free(Class<T> clazz) {
        TypeProperties<?> d = registered.remove(clazz);
        if (d != null) idFactory.unsafeFree(d.id);
        return (TypeProperties<T>) d;
    }
}
