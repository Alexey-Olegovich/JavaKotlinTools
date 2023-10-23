package alexey.tools.common.events;

public class EventBusWrapper extends EventBus {

    private final EventBus parent;



    public EventBusWrapper(final EventBus eventBus) {
        parent = eventBus;
    }



    @Override
    public void run() {
        parent.run();
        super.run();
    }

    @Override
    public void clear() {
        parent.pendingNotify.clear();
        super.clear();
    }



    @SuppressWarnings("unchecked")
    @Override
    protected Group createGroup(Class type) {
        return new SyncGroupWrapper(parent.register(type));
    }



    private class SyncGroupWrapper<T> extends SyncGroup<T> {

        private final Group<T> parent;



        public SyncGroupWrapper(final Group<T> group) {
            parent = group;
        }



        @Override
        public void post(final T event) {
            parent.post(event);
            super.post(event);
        }

        @Override
        public void postNow(final T event) {
            parent.postNow(event);
            super.postNow(event);
        }
    }
}
