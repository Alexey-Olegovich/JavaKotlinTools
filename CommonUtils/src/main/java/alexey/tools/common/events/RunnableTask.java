package alexey.tools.common.events;

public interface RunnableTask extends Task, Runnable {
    @Override
    default boolean run(final float delta) {
        run();
        return true;
    }
}
