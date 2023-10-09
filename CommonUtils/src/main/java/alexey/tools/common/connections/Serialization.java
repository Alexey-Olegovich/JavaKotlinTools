package alexey.tools.common.connections;

import java.nio.ByteBuffer;

public interface Serialization<R, W> {
    void write(Connection<R, W> connection, ByteBuffer output, W source);
        R read(Connection<R, W> connection, ByteBuffer input);
    default Serialization<R, W> copy() { return this; }
    default void dispose() {}
}
