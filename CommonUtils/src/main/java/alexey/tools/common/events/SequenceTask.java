package alexey.tools.common.events;

import org.jetbrains.annotations.NotNull;

public class SequenceTask implements Task {

    private final Task[] tasks;
    private int cursor = 0;
    private Task current;



    public SequenceTask(@NotNull Task[] tasks) {
        this.tasks = tasks;
        current = tasks[0];
    }



    @Override
    public boolean run(final float delta) {
        if (current.run(delta)) {
            if (++cursor == tasks.length) return true;
            current = tasks[cursor];
        }
        return false;
    }
}
