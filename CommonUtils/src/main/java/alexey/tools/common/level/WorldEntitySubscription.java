package alexey.tools.common.level;

import alexey.tools.common.collections.ImmutableIntSet;
import alexey.tools.common.collections.IntSet;
import alexey.tools.common.collections.ObjectContainer;
import alexey.tools.common.collections.ObservableObjectContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

public class WorldEntitySubscription implements EntitySubscription {

    private final IntSet include;
    private final ObservableObjectContainer<Entity> entities = new ObservableObjectContainer<>();

    

    public WorldEntitySubscription(IntSet i) {
        include = i;
    }



    ObjectContainer<Entity>.Entry<Entity> add(@NotNull Entity entity) {
        ImmutableIntSet entityComponentTypes = entity.getComponentTypes();
        return entityComponentTypes.contains(include) ? entities.addEntry(entity) : null;
    }

    @Override
    public Collection<Entity> getEntities() {
        return entities;
    }

    @Override
    public void addInsertListener(Consumer<Entity> listener) {
        entities.addInsertListener(listener);
    }

    @Override
    public void addRemoveListener(Consumer<Entity> listener) {
        entities.addRemoveListener(listener);
    }

    @Override
    public void removeInsertListener(Consumer<Entity> listener) {
        entities.removeInsertListener(listener);
    }

    @Override
    public void removeRemoveListener(Consumer<Entity> listener) {
        entities.removeRemoveListener(listener);
    }
}
