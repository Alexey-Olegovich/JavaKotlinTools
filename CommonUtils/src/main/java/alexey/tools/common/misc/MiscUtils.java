package alexey.tools.common.misc;

import alexey.tools.common.collections.ObjectList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MiscUtils {

    @NotNull
    public static List<IOException> closeAll(@NotNull Iterable<? extends Closeable> closeables) {
        ObjectList<IOException> errors = null;
        for (Closeable closeable : closeables) try {
            closeable.close();
        } catch (IOException error) {
            if (errors == null) errors = new ObjectList<>(4);
            errors.add(error);
        }
        return errors == null ? Collections.emptyList() : errors;
    }



    @NotNull
    @Contract(value = " -> new", pure = true)
    public static Object newObject() {
        return new Object();
    }
}
