package alexey.tools.common.events;

import alexey.tools.common.context.ImmutableVariable;

public class WhileZeroTask implements Task {

    protected final ImmutableVariable variable;
    protected final Runnable action;



    public WhileZeroTask(final ImmutableVariable v, final Runnable a) {
        variable = v;
        action = a;
    }



    @Override
    public boolean run(float delta) {
        action.run();
        return variable.toFloat() > 0F;
    }
}
