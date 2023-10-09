package alexey.tools.common.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class Sync extends AbstractQueuedSynchronizer {

    public Sync(final int count) {
        setState(count);
    }



    public int getCount() {
        return getState();
    }

    public void setCount(final int count) {
        setState(count);
    }

    public boolean setCount(final int count, final int condition) {
        return compareAndSetState(condition, count);
    }

    @Override
    protected int tryAcquireShared(final int acquires) {
        return getState() <= acquires ? 1 : -1;
    }

    @Override
    protected boolean tryReleaseShared(final int releases) {
        for (;;) {
            final int state = getState();
            if (state < 1) return false;
            final int next = state - releases;
            if (compareAndSetState(state, next)) return true;
        }
    }

}
