package alexey.tools.common.events;

import java.util.concurrent.Executor;

public class AsyncEventBus extends SyncEventBus {

    protected final Executor executor;



    public AsyncEventBus(final Executor executor) {
        this.executor = executor;
    }



    protected Group createGroup(Class type) {
        return new AsyncGroup();
    }



    protected class AsyncGroup <T> extends DefaultGroup<T> {
        @Override
        public void post(final T event) {
            executor.execute(() -> postNow(event));
        }
    }
}
