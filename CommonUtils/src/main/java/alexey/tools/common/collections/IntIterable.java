package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.util.Iterator;

public interface IntIterable extends Iterable<Integer> {

    default IntIterator intIterator() {
        return IntIterator.EMPTY;
    }

    @NotNull
    @Override
    default Iterator<Integer> iterator() {
        return intIterator();
    }
}
