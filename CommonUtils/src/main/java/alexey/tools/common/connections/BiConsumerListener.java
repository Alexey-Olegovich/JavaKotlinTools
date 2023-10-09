package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;
import java.util.function.BiConsumer;

public class BiConsumerListener <R, W> implements Connection.Listener <R, W> {

    private final BiConsumer<Connection<R, W>, R> consumer;
    private final R onDisconnect;



    public BiConsumerListener(BiConsumer<Connection<R, W>, R> consumer, R onDisconnect) {
        if (consumer == null) throw new NullPointerException("BiConsumer can't be null!");
        this.onDisconnect = onDisconnect;
        this.consumer = consumer;
    }

    public BiConsumerListener(BiConsumer<Connection<R, W>, R> consumer) {
        this(consumer, null);
    }



    @Override
    public void onRead(@NotNull Connection<R, W> connection, R message) {
        consumer.accept(connection, message);
    }

    @Override
    public void onDisconnect(@NotNull Connection<R, W> connection) {
        if (onDisconnect == null) return;
        consumer.accept(connection, onDisconnect);
    }
}
