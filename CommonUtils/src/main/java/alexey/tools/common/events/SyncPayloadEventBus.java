package alexey.tools.common.events;

import alexey.tools.common.collections.ObjectList;
import alexey.tools.common.misc.ArrayUtils;
import alexey.tools.common.misc.Injector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SyncPayloadEventBus <P> implements Injector {

    protected final ConcurrentHashMap<Class, Group> groups = new ConcurrentHashMap<>();
    protected final Function<Class, Group> computeFunction = this::createGroup;



    @SuppressWarnings("unchecked")
    public <T> Group<P, T> register(final Class<T> type) {
        return groups.computeIfAbsent(type, computeFunction);
    }

    @SuppressWarnings("unchecked")
    public <T> void register(final Class<? extends T> type, final BiConsumer<P, T> listener) {
        groups.computeIfAbsent(type, computeFunction).addListener(listener);
    }

    @Override
    public void inject(@NotNull final Object listeners) {
        for (final MethodData method : getListeners(listeners.getClass()))
            register(method.type).addListener(new MethodListener<>(method, listeners));
    }

    @SuppressWarnings("unchecked")
    public void post(final P payload, @NotNull final Object message) {
        final Group group = groups.get(message.getClass());
        if (group == null) return;
        group.post(payload, message);
    }

    public void post(final P payload, @NotNull final Iterable<Object> messages) {
        for (final Object message : messages) post(payload, message);
    }

    @SuppressWarnings("unchecked")
    public void postNow(final P payload, @NotNull final Object event) {
        final Group group = groups.get(event.getClass());
        if (group == null) return;
        group.postNow(payload, event);
    }

    public void postNow(final P payload, @NotNull final Iterable<Object> messages) {
        for (final Object message : messages) postNow(payload, message);
    }

    @SuppressWarnings("unchecked")
    public <T> Group<P, T> get(Class<T> type) {
        return groups.get(type);
    }

    public void clear() {
        groups.clear();
    }



    protected Group createGroup(Class type) {
        return new DefaultGroup();
    }



    public static class DefaultGroup <P, T> implements Group<P, T> {

        protected volatile BiConsumer[] listeners = new BiConsumer[0];
        protected final Object lock = new Object();



        @Override
        public void post(final P payload, final T event) {
            postNow(payload, event);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void postNow(final P payload, final T event) {
            for (BiConsumer listener : listeners) listener.accept(payload, event);
        }

        @Override
        public void addListener(final BiConsumer<P, T> listener) {
            synchronized(lock) { listeners = ArrayUtils.plus(listeners, listener); }
        }

        @Override
        public void removeListener(final BiConsumer<P, T> listener) {
            synchronized(lock) { listeners = ArrayUtils.minus(listeners, listener); }
        }
    }



    public interface Group <P, T> {
        default void post(final P payload, final T event) {}
        default void postNow(final P payload, final T event) {}
        default void addListener(final BiConsumer<P, T> listener) {}
        default void removeListener(final BiConsumer<P, T> listener) {}

        Group NULL = new Group() {};

        @SuppressWarnings("unchecked")
        static <P, T> Group<P, T> getDefault() {
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
                result.unsafeAdd(count == 2 ?
                        new MethodData(method.getParameterTypes()[1], method) :
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
