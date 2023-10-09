package alexey.tools.common.context;

import alexey.tools.common.events.TaskManager;
import java.util.function.Consumer;

public class WhileZeroListener extends WhileNotZeroListener implements Consumer<ImmutableVariable> {

    public WhileZeroListener(final TaskManager tm, final ImmutableVariable v, final Runnable a) {
        super(tm, v, a);
    }



    @Override
    protected boolean isDone() {
        return variable.toFloat() != 0F;
    }
}
