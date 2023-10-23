package alexey.tools.common.events;

import alexey.tools.common.collections.ObjectList;
import alexey.tools.common.misc.ArrayUtils;
import alexey.tools.common.misc.Injector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class SyncEventBus implements Injector {

    protected final ConcurrentHashMap<Class, Group> groups = new ConcurrentHashMap<>();
    protected final Function<Class, Group> computeFunction = this::createGroup;



    @SuppressWarnings("unchecked")
    public <T> Group<T> register(final Class<T> type) {
        return groups.computeIfAbsent(type, computeFunction);
    }

    public <T> void register(final Class<? extends T> type, final Consumer<T> listener) {
        register(type).addListener(listener);
    }

    @Override
    public void inject(@NotNull final Object listeners) {
        for (final MethodData method : getListeners(listeners.getClass()))
            register(method.type).addListener(new MethodListener<>(method, listeners));
    }

    @SuppressWarnings("unchecked")
    public void post(@NotNull final Object event) {
        final Group group = groups.get(event.getClass());
        if (group == null) return;
        group.post(event);
    }

    public void post(@NotNull final Iterable<Object> messages) {
        for (final Object message : messages) post(message);
    }

    @SuppressWarnings("unchecked")
    public void postNow(@NotNull final Object event) {
        final Group group = groups.get(event.getClass());
        if (group == null) return;
        group.postNow(event);
    }

    public void postNow(@NotNull final Iterable<Object> messages) {
        for (final Object message : messages) postNow(message);
    }

    @SuppressWarnings("unchecked")
    public <T> Group<T> get(final Class<T> type) {
        return groups.get(type);
    }

    public void clear() {
        groups.clear();
    }



    protected Group createGroup(Class type) {
        return new DefaultGroup();
    }



    public static class DefaultGroup <T> implements Group<T> {

        protected volatile Consumer[] listeners = new Consumer[0];
        protected final Object lock = new Object();



        @Override
        public void post(T event) {
            postNow(event);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void postNow(final T event) {
            for (Consumer listener : listeners) listener.accept(event);
        }

        @Override
        public void addListener(final Consumer<? super T> listener) {
            synchronized(lock) { listeners = ArrayUtils.plus(listeners, listener); }
        }

        @Override
        public void removeListener(final Consumer<? super T> listener) {
            synchronized(lock) { listeners = ArrayUtils.minus(listeners, listener); }
        }
    }



    public interface Group <T> {
        default void post(final T event) {}
        default void postNow(final T event) {}
        default void addListener(final Consumer<? super T> listener) {}
        default void removeListener(final Consumer<? super T> listener) {}

        AsyncEventBus.Group NULL = new AsyncEventBus.Group() {};

        @SuppressWarnings("unchecked")
        static <T> AsyncEventBus.Group<T> getDefault() {
            return NULL;
        }
    }



    public static final ConcurrentHashMap<Class, Iterable<MethodData>> methodCache = new ConcurrentHashMap<>();

    public static Iterable<MethodData> getListeners(final Class type) {
        return methodCache.computeIfAbsent(type, createCache);
    }

    public static final Function<Class, Iterable<MethodData>> createCache = type -> {
        final ObjectList<MethodData> result = new ObjectList<>();
        while (type != Object.class) {
            final Method[] methods = type.getDeclaredMethods();
            for (final Method method : methods) {
                Listener annotation = method.getAnnotation(Listener.class);
                if (annotation == null) continue;
                final int count = method.getParameterCount();
                if (count > 1) continue;
                result.unsafeAdd(count == 1 ?
                        new MethodData(method.getParameterTypes()[0], method) :
                        new IgnoredMethodData(annotation.value(), method));
            }
            type = type.getSuperclass();
        }
        return result.isEmpty() ? Collections.emptyList() : result;
    };

    public static void clearCache() {
        methodCache.clear();
    }
}
