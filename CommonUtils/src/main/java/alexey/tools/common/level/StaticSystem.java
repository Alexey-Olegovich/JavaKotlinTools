package alexey.tools.common.level;

import java.io.Closeable;
import java.io.IOException;

public interface StaticSystem extends Closeable {
    default void initialize() {}
    default void close() throws IOException {}
}
