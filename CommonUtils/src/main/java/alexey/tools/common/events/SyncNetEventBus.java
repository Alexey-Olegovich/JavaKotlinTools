package alexey.tools.common.events;

import alexey.tools.common.collections.ObjectList;
import alexey.tools.common.misc.ArrayUtils;
import alexey.tools.common.misc.Injector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SyncNetEventBus implements Injector {

    protected final ConcurrentHashMap<Class, Group> groups = new ConcurrentHashMap<>();
    protected final Function<Class, Group> computeFunction = this::createGroup;



    @SuppressWarnings("unchecked")
    public <T> Group<T> register(final Class<T> type) {
        return groups.computeIfAbsent(type, computeFunction);
    }

    public <T> void register(final Class<? extends T> type, final NetConsumer<T> listener) {
        register(type).addListener(listener);
    }

    @Override
    public void inject(@NotNull final Object listeners) {
        for (final MethodData method : getListeners(listeners.getClass()))
            register(method.type).addListener(new MethodListener<>(method, listeners));
    }

    @SuppressWarnings("unchecked")
    public void post(final int id, @NotNull final Object message) {
        final Group group = groups.get(message.getClass());
        if (group == null) return;
        group.post(id, message);
    }

    public void post(final int id, @NotNull final Iterable<Object> messages) {
        for (final Object message : messages) post(id, message);
    }

    @SuppressWarnings("unchecked")
    public void postNow(final int id, @NotNull final Object event) {
        final Group group = groups.get(event.getClass());
        if (group == null) return;
        group.postNow(id, event);
    }

    public void postNow(final int id, @NotNull final Iterable<Object> messages) {
        for (final Object message : messages) postNow(id, message);
    }

    @SuppressWarnings("unchecked")
    public <T> Group<T> get(Class<T> type) {
        return groups.get(type);
    }

    public void clear() {
        groups.clear();
    }



    protected Group createGroup(Class type) {
        return new DefaultGroup();
    }



    public static class DefaultGroup <T> implements Group <T> {

        protected volatile NetConsumer[] listeners = new NetConsumer[0];
        protected final Object lock = new Object();



        @Override
        public void post(final int id, final T event) {
            postNow(id, event);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void postNow(final int id, final T event) {
            for (NetConsumer listener : listeners) listener.accept(id, event);
        }

        @Override
        public void addListener(final NetConsumer<? super T> listener) {
            synchronized(lock) { listeners = ArrayUtils.plus(listeners, listener); }
        }

        @Override
        public void removeListener(final NetConsumer<? super T> listener) {
            synchronized(lock) { listeners = ArrayUtils.minus(listeners, listener); }
        }
    }



    public interface Group <T> {
        default void post(final int id, final T event) {}
        default void postNow(final int id, final T event) {}
        default void addListener(final NetConsumer<? super T> listener) {}
        default void removeListener(final NetConsumer<? super T> listener) {}

        Group NULL = new Group() {};

        @SuppressWarnings("unchecked")
        static <T> Group<T> getDefault() {
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
                NetListener annotation = method.getAnnotation(NetListener.class);
                if (annotation == null) continue;
                final int count = method.getParameterCount();
                if (count == 0 || count > 2) continue;
                final Class[] types = method.getParameterTypes();
                if (types[0] != int.class) continue;
                result.unsafeAdd(count == 2 ?
                        new MethodData(types[1], method) :
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
