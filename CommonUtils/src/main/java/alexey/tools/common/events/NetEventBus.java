package alexey.tools.common.events;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetEventBus extends SyncNetEventBus implements Runnable {

    protected final ConcurrentLinkedQueue<Payload> pendingNotify = new ConcurrentLinkedQueue<>();



    @Override
    public void run() {
        Payload payload;
        while ((payload = pendingNotify.poll()) != null) payload.notifyListeners();
    }

    public void post(final NetEventBus eventBus) {
        if (eventBus == null) return;
        Payload payload;
        while ((payload = eventBus.pendingNotify.poll()) != null) post(payload.id, payload.event);
    }

    public void clear() {
        groups.clear();
        pendingNotify.clear();
    }



    protected Group createGroup(Class type) {
        return new SyncGroup();
    }



    protected class SyncGroup<T> extends DefaultGroup <T> {
        @Override
        public void post(final int id, final T event) {
            pendingNotify.add(new Payload<>(id, event, listeners));
        }
    }

    private static class Payload<T> {

        private final int id;
        private final T event;
        private final NetConsumer[] listeners;



        public Payload(final int id, final T event, final NetConsumer[] listeners) {
            this.id = id;
            this.event = event;
            this.listeners = listeners;
        }



        @SuppressWarnings("unchecked")
        public void notifyListeners() {
            for (NetConsumer listener : listeners) listener.accept(id, event);
        }
    }
}
