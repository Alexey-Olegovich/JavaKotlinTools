package alexey.tools.common.events;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class EventBus extends SyncEventBus implements Runnable {

    protected final ConcurrentLinkedQueue<Payload> pendingNotify = new ConcurrentLinkedQueue<>();



    public void post(final EventBus eventBus) {
        if (eventBus == null || eventBus == this) return;
        Payload payload;
        while ((payload = eventBus.pendingNotify.poll()) != null) post(payload.event);
    }

    @Override
    public void run() {
        Payload payload;
        while ((payload = pendingNotify.poll()) != null) payload.notifyListeners();
    }

    @Override
    public void clear() {
        groups.clear();
        pendingNotify.clear();
    }



    protected Group createGroup(Class type) {
        return new SyncGroup();
    }



    protected class SyncGroup <T> extends DefaultGroup<T> {
        @Override
        public void post(final T event) {
            pendingNotify.add(new Payload<>(event, listeners));
        }
    }

    protected static class Payload<T> {

        protected final T event;
        protected final Consumer[] listeners;



        public Payload(final T event, final Consumer[] listeners) {
            this.event = event;
            this.listeners = listeners;
        }



        @SuppressWarnings("unchecked")
        public void notifyListeners() {
            for (Consumer listener : listeners) listener.accept(event);
        }
    }
}
