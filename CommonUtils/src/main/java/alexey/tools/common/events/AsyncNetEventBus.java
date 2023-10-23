package alexey.tools.common.events;

import java.util.concurrent.Executor;

public class AsyncNetEventBus extends SyncNetEventBus {

    protected final Executor executor;



    public AsyncNetEventBus(final Executor executor) {
        this.executor = executor;
    }



    protected Group createGroup(Class type) {
        return new AsyncGroup();
    }



    protected class AsyncGroup <T> extends DefaultGroup <T> {
        @Override
        public void post(final int id, final T event) {
            executor.execute(() -> postNow(id, event));
        }
    }
}
