package alexey.tools.common.connections;

public interface SyncConnection<R, W> extends Connection<R, W> {
    default int update() { return -1; }
    default int update(final long milliseconds) { return -1; }
}
