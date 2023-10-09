package alexey.tools.common.connections;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class SelectableConnection<R, W> extends ListenableConnection<R, W> {

    private SelectionKey selectionKey;
    private final Object operationLock = new Object();



    protected SelectableConnection(final Listener<R, W> listener) {
        super(listener);
    }

    protected SelectableConnection(final Listener<R, W> listener, SelectionKey key) {
        super(listener);
        setSelectionKey(key);
    }

    protected SelectableConnection(SelectionKey key) {
        super();
        setSelectionKey(key);
    }

    protected SelectableConnection() {
        super();
    }



    protected void wantOperation(final int op, final boolean value) {
        if (value)
            interestOpsOr(op); else
            interestOpsAnd(~op);
        wakeup();
    }

    protected void setSelectionKey(final SelectionKey key) {
        if (key == null) throw new NullPointerException("SelectionKey can't be null!");
        if (selectionKey != null) throw new IllegalStateException("SelectionKey already set!");
        selectionKey = key;
    }

    protected boolean sameChannel(SelectableChannel channel) {
        return selectionKey == null || selectionKey.channel() == channel;
    }

    protected int getReadyOps() {
        return selectionKey.readyOps();
    }

    protected void setOperations(final int ops) {
        selectionKey.interestOps(ops);
    }

    protected void interestOpsAnd(final int op) {
        synchronized (operationLock) {
            setOperations(selectionKey.interestOps() & op);
        }
    }

    protected void interestOpsOr(final int op) {
        synchronized (operationLock) {
            setOperations(selectionKey.interestOps() | op);
        }
    }

    protected boolean hasOperation(final int op) {
        return (selectionKey.interestOps() & op) != 0;
    }

    protected void wakeup() {
        selectionKey.selector().wakeup();
    }



    @Override
    public boolean wantRead() {
        return hasOperation(SelectionKey.OP_READ);
    }

    @Override
    public boolean wantConnect() {
        return hasOperation(SelectionKey.OP_CONNECT);
    }

    @Override
    public void wantRead(final boolean value) {
        wantOperation(SelectionKey.OP_READ, value);
    }

    @Override
    public void wantConnect(final boolean value) {
        wantOperation(SelectionKey.OP_CONNECT, value);
    }
}
