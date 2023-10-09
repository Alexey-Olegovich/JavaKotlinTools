package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public interface Connection<R, W> {

    interface Listener <R, W> {
        default void onConnect(@NotNull final Connection<R, W> connection) {}
        default void onDisconnect(@NotNull final Connection<R, W> connection) {}

        default void onRead(@NotNull final Connection<R, W> connection, final R message) {}
        default void onWrite(@NotNull final Connection<R, W> connection) {}

        default void onError(@NotNull final Connection<R, W> connection, @NotNull final Throwable error) {}

        Listener DEFAULT = new Listener() {};

        @SuppressWarnings("unchecked")
        static <R, W> Listener<R, W> defaultListener() {
            return DEFAULT;
        }
    }

    default void setListener(final Listener<R, W> listener) {}
    @SuppressWarnings("unchecked")
    default Listener<R, W> getListener() { return Listener.DEFAULT; }
    @SuppressWarnings("unchecked")
    default void resetListener() { setListener(Listener.DEFAULT); }

    default void send(final W message) throws IOException {}

    default void wantRead(final boolean value) {}
    default void wantConnect(final boolean value) {}

    default boolean wantRead() { return false; }
    default boolean wantConnect() { return false; }

    default void initialize(final String address) throws IOException {}
    default String getAddress() { return ""; }

    default void shutdown() {}

    default boolean isTerminating() { return false; }
    default boolean isTerminated() { return true; }
    default boolean isWorking() { return !isTerminated() && !isTerminating(); }

    default void awaitTermination(final int milliseconds) throws InterruptedException {}
    default void awaitTermination() throws InterruptedException { awaitTermination(0); }

    default Object attachment() { return null; }
    default void attach(final Object ob) {}

    Connection DEFAULT = new Connection() {};

    @SuppressWarnings("unchecked")
    static <R, W> Connection<R, W> defaultConnection() {
        return DEFAULT;
    }
}
