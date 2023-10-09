package alexey.tools.common.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public interface ImmutableWaiter {

    void await() throws InterruptedException;

    void await(final int arg) throws InterruptedException;

    void forceAwait();

    boolean await(final long timeout, @NotNull final TimeUnit unit) throws InterruptedException;

    boolean await(final int arg, final long timeout, @NotNull final TimeUnit unit) throws InterruptedException;

    boolean isBusy();

    boolean isDone();

    boolean isDone(final int arg);

    int count();
}
