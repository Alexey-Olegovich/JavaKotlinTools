package alexey.tools.common.identity;

import java.lang.reflect.InvocationTargetException;

public class TypeProperties<T> {

    final public Class<T> type;
    final public int id;



    public TypeProperties(Class<T> type, int id) {
        this.type = type;
        this.id = id;
    }



    public T newInstance() throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        return type.getDeclaredConstructor().newInstance();
    }
}
