package alexey.tools.common.events;

public class DelayTask implements Task {

    private float delay;



    public DelayTask(final float delay) {
        this.delay = delay;
    }



    @Override
    public boolean run(final float delta) {
        delay -= delta;
        return delay <= 0F;
    }
}
