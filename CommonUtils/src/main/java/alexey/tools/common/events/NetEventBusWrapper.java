package alexey.tools.common.events;

public class NetEventBusWrapper extends NetEventBus {

    private final NetEventBus parent;



    public NetEventBusWrapper(final NetEventBus eventBus) {
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
        public void post(final int id, final T event) {
            parent.post(id, event);
            super.post(id, event);
        }

        @Override
        public void postNow(final int id, final T event) {
            parent.postNow(id, event);
            super.postNow(id, event);
        }
    }
}
