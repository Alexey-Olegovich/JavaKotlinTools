package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public interface ParallelExecutorService extends BlankExecutorService {

    @NotNull
    @Override
    default <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks,
                                          long timeout, @NotNull TimeUnit unit) throws InterruptedException {

        final long nanos = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanos;
        List<Future<T>> futures = Collections.emptyList();
        int j = 0;
        try {
            futures = submitAll(tasks);
            for (final int size = futures.size(); j < size; j++) {
                Future<T> f = futures.get(j);
                if (f.isDone()) continue;
                try { f.get(deadline - System.nanoTime(), NANOSECONDS); }
                catch (CancellationException | ExecutionException ignore) {}
                catch (TimeoutException timedOut) {
                    BlankExecutorService.cancelAll(futures, j);
                    break;
                }
            }
            return futures;
        } catch (Throwable t) {
            BlankExecutorService.cancelAll(futures);
            throw t;
        }
    }
}
