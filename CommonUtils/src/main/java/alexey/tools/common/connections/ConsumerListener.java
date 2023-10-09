package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;

public class ConsumerListener <R, W> implements Connection.Listener <R, W> {

    private final Consumer<R> consumer;



    public ConsumerListener(Consumer<R> consumer) {
        if (consumer == null) throw new NullPointerException("Consumer can't be null!");
        this.consumer = consumer;
    }



    @Override
    public void onRead(@NotNull Connection<R, W> connection, R message) {
        consumer.accept(message);
    }
}
