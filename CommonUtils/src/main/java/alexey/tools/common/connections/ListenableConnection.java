package alexey.tools.common.connections;

public abstract class ListenableConnection<R, W> implements Connection<R, W> {

    private volatile Listener<R, W> listener;
    protected volatile Object attachment;



    protected ListenableConnection(final Listener<R, W> listener) {
        setListener(listener);
    }

    @SuppressWarnings("unchecked")
    protected ListenableConnection() {
        listener = Listener.DEFAULT;
    }



    protected void notifyError(final Throwable e) {
        listener.onError(this, e);
    }

    protected void notifyErrorSilently(final Throwable e) {
        try { notifyError(e); } catch (final Throwable ignored) {}
    }

    protected void notifyDisconnect() {
        listener.onDisconnect(this);
    }

    protected void notifyRead(final R message) {
        listener.onRead(this, message);
    }

    protected void notifyConnect() {
        listener.onConnect(this);
    }

    protected void notifyWrite() {
        listener.onWrite(this);
    }



    @Override
    public void setListener(final Listener<R, W> listener) {
        if (listener == null) throw new NullPointerException("Listener can't be null!");
        this.listener = listener;
    }

    @Override
    public Listener<R, W> getListener() {
        return listener;
    }

    @Override
    public void attach(Object ob) {
        attachment = ob;
    }

    @Override
    public Object attachment() {
        return attachment;
    }
}
