package alexey.tools.common.concurrent;

import alexey.tools.common.collections.ObjectList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public interface BlankExecutorService extends ExecutorService {

    @NotNull
    @Override
    default  <T> Future<T> submit(@NotNull Callable<T> task) {
        RunnableFuture<T> runnableFuture = new FutureTask<>(task);
        execute(runnableFuture);
        return runnableFuture;
    }

    @NotNull
    @Override
    default <T> Future<T> submit(@NotNull Runnable task, T result) {
        RunnableFuture<T> runnableFuture = new FutureTask<>(task, result);
        execute(runnableFuture);
        return runnableFuture;
    }

    @NotNull
    @Override
    default Future<?> submit(@NotNull Runnable task) {
        RunnableFuture<?> runnableFuture = new FutureTask<>(task, null);
        execute(runnableFuture);
        return runnableFuture;
    }



    default <T> List<Future<T>> submitAll(@NotNull Collection<? extends Callable<T>> tasks) {
        ObjectList<Future<T>> results = new ObjectList<>(tasks.size());
        for (Callable<T> task : tasks) results.unsafeAdd(submit(task));
        return results;
    }

    default <T> T doInvokeAny(Collection<? extends Callable<T>> tasks,
                              boolean timed,
                              long nanos) throws InterruptedException, ExecutionException, TimeoutException {
        if (tasks == null)
            throw new NullPointerException();
        int ntasks = tasks.size();
        if (ntasks == 0)
            throw new IllegalArgumentException();
        ArrayList<Future<T>> futures = new ArrayList<>(ntasks);
        ExecutorCompletionService<T> ecs = new ExecutorCompletionService<>(this);
        try {
            ExecutionException ee = null;
            final long deadline = timed ? System.nanoTime() + nanos : 0L;
            Iterator<? extends Callable<T>> it = tasks.iterator();

            futures.add(ecs.submit(it.next()));
            --ntasks;
            int active = 1;

            for (;;) {
                Future<T> f = ecs.poll();
                if (f == null) {
                    if (ntasks > 0) {
                        --ntasks;
                        futures.add(ecs.submit(it.next()));
                        ++active;
                    }
                    else if (active == 0)
                        break;
                    else if (timed) {
                        f = ecs.poll(nanos, NANOSECONDS);
                        if (f == null)
                            throw new TimeoutException();
                        nanos = deadline - System.nanoTime();
                    }
                    else
                        f = ecs.take();
                }
                if (f != null) {
                    --active;
                    try {
                        return f.get();
                    } catch (ExecutionException eex) {
                        ee = eex;
                    } catch (RuntimeException rex) {
                        ee = new ExecutionException(rex);
                    }
                }
            }

            if (ee == null)
                ee = new ExecutionException(null);
            throw ee;

        } finally {
            cancelAll(futures);
        }
    }



    @NotNull
    @Override
    default  <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Future<T>> futures = Collections.emptyList();
        try {
            futures = submitAll(tasks);
            for (Future<T> f : futures) if (!f.isDone())
                try { f.get(); } catch (CancellationException | ExecutionException ignore) { }
            return futures;
        } catch (Throwable t) {
            cancelAll(futures);
            throw t;
        }
    }

    @NotNull
    @Override
    default  <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks,
                                           long timeout,
                                           @NotNull TimeUnit unit) throws InterruptedException {

        final long nanos = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanos;
        ObjectList<Future<T>> futures = new ObjectList<>(tasks.size());
        int j = 0;
        timedOut: try {
            for (Callable<T> t : tasks) {
                if (deadline - System.nanoTime() <= 0L) break timedOut;
                futures.unsafeAdd(submit(t));
            }
            for (final int size = futures.size(); j < size; j++) {
                Future<T> f = futures.get(j);
                if (f.isDone()) continue;
                try { f.get(deadline - System.nanoTime(), NANOSECONDS); }
                catch (CancellationException | ExecutionException ignore) {}
                catch (TimeoutException timedOut) { break timedOut; }
            }
            return futures;
        } catch (Throwable t) {
            cancelAll(futures);
            throw t;
        }
        cancelAll(futures, j);
        return futures;
    }

    @NotNull
    @Override
    default  <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        try {
            return doInvokeAny(tasks, false, 0);
        } catch (TimeoutException cannotHappen) {
            assert false;
            return null;
        }
    }

    @Override
    default  <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return doInvokeAny(tasks, true, unit.toNanos(timeout));
    }



    static <T> void cancelAll(@NotNull Iterable<Future<T>> futures) {
        for (Future<T> f : futures) f.cancel(true);
    }

    static <T> void cancelAll(@NotNull List<Future<T>> futures, int j) {
        for (final int size = futures.size(); j < size; j++)
            futures.get(j).cancel(true);
    }
}
