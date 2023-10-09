package alexey.tools.common.identity;

public interface ImmutableTypeFactory {
    <T> TypeProperties<T> get(Class<T> clazz);
    int size();
}
