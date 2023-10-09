package alexey.tools.common.level;

import alexey.tools.common.collections.CompactObjectStorage;
import alexey.tools.common.collections.IntSet;
import alexey.tools.common.collections.ObjectContainer;
import alexey.tools.common.collections.ObjectList;
import alexey.tools.common.identity.ClassIndex;
import alexey.tools.common.identity.IndexKey;
import alexey.tools.common.identity.TypeProperties;
import alexey.tools.common.misc.Injector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class World implements Closeable {
    
    private final ClassIndex identifier = new ClassIndex();
    private final ObjectList<Injector> injectors = new ObjectList<>(4);
    private int idFactory = 0;
    private final ObjectList<WorldEntity> entityPool = new ObjectList<>();
    
    private final ObjectContainer<Entity> entities = new ObjectContainer<>();
    private final ObjectList<StaticSystem> systems = new ObjectList<>();
    private final ObjectList<DynamicSystem> dynamicSystems = new ObjectList<>();
    
    private final CompactObjectStorage<ObjectList<WorldEntitySubscription>> subscriptionsByOne = new CompactObjectStorage<>();
    private final HashMap<IndexKey, WorldEntitySubscription> subscriptionsByAll = new HashMap<>();
    
    
    
    public Entity createEntity() {
        return entityPool.isEmpty() ? new WorldEntity() : entityPool.removeLast().initialize();
    }
    
    public Collection<Entity> getEntities() {
        return entities;
    }
    
    public void addSystem(DynamicSystem system) {
        for (Injector injector: injectors) injector.inject(system);
        system.initialize();
        systems.add(system);
        dynamicSystems.add(system);
    }

    public void addSystem(StaticSystem system) {
        for (Injector injector: injectors) injector.inject(system);
        system.initialize();
        systems.add(system);
    }

    public void addInjector(Injector injector) {
        injectors.add(injector);
    }

    public void removeInjector(Injector injector) {
        injectors.removeReference(injector);
    }

    public ClassIndex getIdentifier() {
        return identifier;
    }

    public void update(final float deltaTime) {
        for (DynamicSystem dynamicSystem : dynamicSystems)
            dynamicSystem.update(deltaTime);
    }

    public EntitySubscription obtainEntitySubscription(Class<?> type) {
        return obtainEntitySubscription(new Class[] { type });
    }

    public EntitySubscription obtainEntitySubscription(Class<?>[] types) {
        final IndexKey key = identifier.obtain(types);
        WorldEntitySubscription subscription = subscriptionsByAll.get(key);
        if (subscription != null) return subscription;
        final IntSet componentTypes = new IntSet();
        subscription = new WorldEntitySubscription(componentTypes);
        subscriptionsByAll.put(key, subscription);
        for (Class<?> type : types) {
            final int componentType = identifier.obtain(type);
            obtainComponentStorage(componentType).add(subscription);
            componentTypes.add(componentType);
        }
        return subscription;
    }

    @Override
    public void close() throws IOException {
        IOException iError = null;
        Throwable otherError = null;
        for (StaticSystem system : systems) {
            try {
                system.close();
            } catch (IOException e) {
                iError = e;
            } catch (Throwable e) {
                otherError = e;
            }
        }
        if (iError != null) throw iError;
        if (otherError != null) throw new RuntimeException(otherError);
    }



    @NotNull
    private ObjectList<WorldEntitySubscription> obtainComponentStorage(final int index) {
        subscriptionsByOne.growIndex(index);
        ObjectList<WorldEntitySubscription> storage = subscriptionsByOne.get(index);
        if (storage != null) return storage;
        storage = new ObjectList<>();
        subscriptionsByOne.justSet(index, storage);
        return storage;
    }



    private class WorldEntity implements Entity {

        private final int id = idFactory++;
        private ObjectContainer<Entity>.Entry<Entity> entitiesEntry = entities.addEntry(this);
        private final IntSet componentTypes = new IntSet();
        private final Data[] data;



        public WorldEntity() {
            int size = identifier.size();
            data = new Data[size];
            while (--size >= 0) data[size] = new Data();
        }

        public WorldEntity initialize() {
            entitiesEntry = entities.addEntry(this);
            componentTypes.clear();
            return this;
        }



        @SuppressWarnings("unchecked")
        @Override
        public <T> T set(final T component) {
            return set((TypeProperties<T>) identifier.obtainProperties(component.getClass()), component);
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T set(@NotNull final TypeProperties<T> typeProperties, final T component) {
            final int id = typeProperties.id;
            final Data d = data[id];
            final Object old = d.component;
            d.component = component;
            if (old != DEFAULT_COMPONENT) return (T) old;
            componentTypes.add(id);
            final List<WorldEntitySubscription> subscriptions = subscriptionsByOne.get(id);
            final List<ObjectContainer<Entity>.Entry<Entity>> entries = d.entries;
            for (WorldEntitySubscription subscription: subscriptions) {
                final ObjectContainer<Entity>.Entry<Entity> entry = subscription.add(this);
                if (entry == null) continue;
                entries.add(entry);
            }
            return null;
        }

        @Override
        public <T> T getOrNull(Class<T> componentType) {
            return getOrNull(identifier.obtainProperties(componentType));
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getOrNull(TypeProperties<T> typeProperties) {
            return (T) data[typeProperties.id].getComponentOrNull();
        }

        @Override
        public <T> T get(Class<T> componentType) {
            return get(identifier.obtainProperties(componentType));
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(TypeProperties<T> typeProperties) {
            return (T) data[typeProperties.id].component;
        }

        @Override
        public <T> T remove(Class<T> componentType) {
            return remove(identifier.obtainProperties(componentType));
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T remove(TypeProperties<T> typeProperties) {
            return (T) data[typeProperties.id].remove();
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public IntSet getComponentTypes() {
            return componentTypes;
        }

        @Override
        public World getWorld() {
            return World.this;
        }

        @Override
        public void delete() {
            if (entitiesEntry == null) return;
            entitiesEntry.remove();
            for (final Data d: data) if (d.component != DEFAULT_COMPONENT) d.justRemove();
            entityPool.add(this);
        }
    }

    private static class Data {

        private Object component = DEFAULT_COMPONENT;
        private final ObjectList<ObjectContainer<Entity>.Entry<Entity>> entries = new ObjectList<>(4);



        @Nullable
        @Contract(pure = true)
        private Object getComponentOrNull() {
            return component == DEFAULT_COMPONENT ? null : component;
        }

        private void justRemove() {
            while (entries.isNotEmpty()) entries.removeLast().remove();
            component = DEFAULT_COMPONENT;
        }

        @Nullable
        private Object remove() {
            if (component == DEFAULT_COMPONENT) return null;
            final Object old = component;
            justRemove();
            return old;
        }
    }

    private static final Object DEFAULT_COMPONENT = new Object();
}
