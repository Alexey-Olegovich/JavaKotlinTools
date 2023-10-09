package alexey.tools.common.context;

import alexey.tools.common.events.TaskManager;
import alexey.tools.common.events.WhileNotZeroTask;
import java.util.function.Consumer;

public class WhileNotZeroListener extends WhileNotZeroTask implements Consumer<ImmutableVariable> {

    private final TaskManager taskManager;
    private byte state = IDLE;



    public WhileNotZeroListener(final TaskManager tm, final ImmutableVariable v, final Runnable a) {
        super(v, a);
        taskManager = tm;
    }



    @Override
    public void accept(final ImmutableVariable v) {
        if (state == IDLE) {
            if (variable.isInvalid() || isDone()) return;
            action.run();
            taskManager.add(this);
            state = BUSY;
        } else {
            state = variable.isInvalid() || isDone() ? DONE : BUSY;
        }
    }

    @Override
    public boolean run(final float delta) {
        if (state == DONE) {
            state = IDLE;
            return true;
        } else {
            action.run();
            return false;
        }
    }

    protected boolean isDone() {
        return variable.toFloat() == 0F;
    }



    private static final byte IDLE = 0;
    private static final byte BUSY = 1;
    private static final byte DONE = 2;
}
