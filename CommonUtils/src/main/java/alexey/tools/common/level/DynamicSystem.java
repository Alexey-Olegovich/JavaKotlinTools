package alexey.tools.common.level;

public interface DynamicSystem extends StaticSystem {
    default void update(final float deltaTime) {}
}
