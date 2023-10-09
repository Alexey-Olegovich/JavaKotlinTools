package alexey.tools.common.identity;

import java.util.function.Supplier;

public class ClassBinder <T> extends ClassValue <T> {

    private final Supplier<T> factory;



    public ClassBinder(final Supplier<T> factory) {
        this.factory = factory;
    }



    @Override
    protected T computeValue(Class<?> type) {
        return factory.get();
    }
}
