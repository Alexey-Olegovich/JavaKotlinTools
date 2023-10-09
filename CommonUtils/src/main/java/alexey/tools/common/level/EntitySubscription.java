package alexey.tools.common.level;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public interface EntitySubscription {

    @SuppressWarnings("unchecked")
    default Collection<Entity> getEntities() {
        return Collections.EMPTY_LIST;
    }

    default void addInsertListener(Consumer<Entity> listener) {

    }

    default void addRemoveListener(Consumer<Entity> listener) {

    }

    default void removeInsertListener(Consumer<Entity> listener) {

    }

    default void removeRemoveListener(Consumer<Entity> listener) {

    }
}
