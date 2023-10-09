package alexey.tools.common.events;

import alexey.tools.common.context.Variable;

public class DecreaseToZeroTask implements Task {

    protected final Variable variable;



    public DecreaseToZeroTask(final Variable v) {
        variable = v;
    }



    @Override
    public boolean run(float delta) {
        return variable.decreaseToZero(delta) <= 0F;
    }
}
