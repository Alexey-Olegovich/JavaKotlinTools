package alexey.tools.common.events;

import alexey.tools.common.collections.ObjectList;
import alexey.tools.common.misc.ArrayUtils;
import alexey.tools.common.misc.Injector;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventBus implements Runnable, Injector {

    protected final ConcurrentHashMap<Class, DefaultGroup> groups = new ConcurrentHashMap<>();
    protected final Function<Class, DefaultGroup> computeFunction = this::createGroup;
    protected final ConcurrentLinkedQueue<Payload> pendingNotify = new ConcurrentLinkedQueue<>();



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
        final DefaultGroup group = groups.get(event.getClass());
        if (group == null) return;
        group.post(event);
    }

    public void post(@NotNull final Iterable<Object> messages) {
        for (final Object message : messages) post(message);
    }

    @SuppressWarnings("unchecked")
    public void postNow(@NotNull final Object event) {
        final DefaultGroup group = groups.get(event.getClass());
        if (group == null) return;
        group.postNow(event);
    }

    public void postNow(@NotNull final Iterable<Object> messages) {
        for (final Object message : messages) postNow(message);
    }

    public void post(final EventBus eventBus) {
        if (eventBus == null || eventBus == this) return;
        Payload payload;
        while ((payload = eventBus.pendingNotify.poll()) != null) post(payload.event);
    }

    @Override
    public void run() {
        Payload payload;
        while ((payload = pendingNotify.poll()) != null) payload.notifyListeners();
    }

    @SuppressWarnings("unchecked")
    public <T> Group<T> get(final Class<T> type) {
        return groups.get(type);
    }

    public void clear() {
        groups.clear();
        pendingNotify.clear();
    }



    protected DefaultGroup createGroup(Class type) {
        return new DefaultGroup();
    }



    protected class DefaultGroup <T> implements Group<T> {

        private volatile Consumer[] listeners = new Consumer[0];
        private final Object lock = new Object();



        @Override
        public void post(final T event) {
            pendingNotify.add(new Payload<>(event, listeners));
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

    private static class Payload<T> {

        private final T event;
        private final Consumer[] listeners;



        public Payload(final T event, final Consumer[] listeners) {
            this.event = event;
            this.listeners = listeners;
        }



        @SuppressWarnings("unchecked")
        public void notifyListeners() {
            for (Consumer listener : listeners) listener.accept(event);
        }
    }

    public interface Group <T> {
        default void post(final T event) {}
        default void postNow(final T event) {}
        default void addListener(final Consumer<? super T> listener) {}
        default void removeListener(final Consumer<? super T> listener) {}

        Group NULL = new Group() {};

        @SuppressWarnings("unchecked")
        static <T> Group<T> getDefault() {
            return NULL;
        }
    }



    private static final ConcurrentHashMap<Class, Iterable<MethodData>> methodCache = new ConcurrentHashMap<>();

    private static Iterable<MethodData> getListeners(final Class type) {
        return methodCache.computeIfAbsent(type, createCache);
    }

    private static final Function<Class, Iterable<MethodData>> createCache = type -> {
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
