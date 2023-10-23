package alexey.tools.common.events;

import java.util.concurrent.Executor;

public class AsyncPayloadEventBus <P> extends SyncPayloadEventBus <P> {

    protected final Executor executor;



    public AsyncPayloadEventBus(final Executor executor) {
        this.executor = executor;
    }



    protected Group createGroup(Class type) {
        return new AsyncGroup();
    }



    protected class AsyncGroup <T> extends DefaultGroup<P, T> {
        @Override
        public void post(final P payload, final T event) {
            executor.execute(() -> postNow(payload, event));
        }
    }
}
