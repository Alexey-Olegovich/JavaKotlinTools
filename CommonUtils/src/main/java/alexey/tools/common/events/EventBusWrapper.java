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
    protected DefaultGroupWrapper createGroup(Class type) {
        return new DefaultGroupWrapper(parent.register(type));
    }



    private class DefaultGroupWrapper<T> extends DefaultGroup<T> {

        private final Group<T> parent;



        public DefaultGroupWrapper(final Group<T> group) {
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
