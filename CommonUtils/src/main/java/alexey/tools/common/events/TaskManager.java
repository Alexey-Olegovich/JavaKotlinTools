package alexey.tools.common.events;

import alexey.tools.common.collections.ObjectList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class TaskManager implements RunnableTask {

    public final ObjectList<Task> tasks = new ObjectList<>();



    @Override
    public boolean run(final float delta) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) if (iterator.next().run(delta)) iterator.remove();
        return tasks.isEmpty();
    }

    @Override
    public void run() {
        run(0F);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public void add(Task... tasks) {
        this.tasks.add(new SequenceTask(tasks));
    }



    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static DelayTask delay(final float delay) {
        return new DelayTask(delay);
    }
}
