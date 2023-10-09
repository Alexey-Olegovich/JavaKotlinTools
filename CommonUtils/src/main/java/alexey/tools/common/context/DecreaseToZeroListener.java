package alexey.tools.common.context;

import alexey.tools.common.events.DecreaseToZeroTask;
import alexey.tools.common.events.TaskManager;
import java.util.function.Consumer;

public class DecreaseToZeroListener extends DecreaseToZeroTask implements Consumer<ImmutableVariable> {

    private final TaskManager taskManager;
    private boolean busy = false;



    public DecreaseToZeroListener(TaskManager tm, Variable v) {
        super(v);
        taskManager = tm;
    }



    @Override
    public void accept(ImmutableVariable v) {
        if (busy) return;
        busy = variable.isValid() && variable.toFloat() > 0F;
        if (busy) taskManager.add(this);
    }

    @Override
    public boolean run(float delta) {
        busy = variable.isValid() && variable.decreaseToZero(delta) > 0F;
        return !busy;
    }
}
