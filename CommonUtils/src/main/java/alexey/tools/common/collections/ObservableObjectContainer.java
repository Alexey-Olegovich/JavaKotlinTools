package alexey.tools.common.collections;

import java.util.function.Consumer;

public class ObservableObjectContainer<V> extends ObjectContainer<V> {

    private final ObjectList<Consumer<V>> insertListeners = new ObjectList<>();
    private final ObjectList<Consumer<V>> removeListeners = new ObjectList<>();



    public void addInsertListener(Consumer<V> listener) {
        insertListeners.add(listener);
    }

    public void removeInsertListener(Consumer<V> listener) {
        insertListeners.removeReference(listener);
    }

    public void addRemoveListener(Consumer<V> listener) {
        removeListeners.add(listener);
    }

    public void removeRemoveListener(Consumer<V> listener) {
        removeListeners.removeReference(listener);
    }



    @Override
    public Entry<V> addEntry(V e) {
        Entry<V> entry = super.addEntry(e);
        for (Consumer<V> listener: insertListeners) listener.accept(e);
        return entry;
    }

    @Override
    public Entry<V> unsafeAdd(V e) {
        Entry<V> entry = super.unsafeAdd(e);
        for (Consumer<V> listener: insertListeners) listener.accept(e);
        return entry;
    }

    @Override
    public boolean add(V t) {
        super.add(t);
        for (Consumer<V> listener: insertListeners) listener.accept(t);
        return true;
    }



    @SuppressWarnings("unchecked")
    @Override
    public void justRemove(int index) {
        final Entry top = entries[--size];
        final Entry target = entries[index];
        entries[size] = target;
        entries[index] = top;
        top.index = index;
        final V value = (V) target.value;
        target.value = null;
        iterator.cursor--;
        for (Consumer<V> listener: removeListeners) listener.accept(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void justRemoveCurrent() {
        final Entry top = entries[--size];
        final Entry target = entries[--iterator.cursor];
        entries[size] = target;
        entries[iterator.cursor] = top;
        top.index = iterator.cursor;
        final V value = (V) target.value;
        target.value = null;
        for (Consumer<V> listener: removeListeners) listener.accept(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entry<V> remove(int index) {
        final Entry top = entries[--size];
        final Entry target = entries[index];
        entries[size] = target;
        entries[index] = top;
        top.index = index;
        final V value = (V) target.value;
        target.value = null;
        iterator.cursor--;
        for (Consumer<V> listener: removeListeners) listener.accept(value);
        return target;
    }
}
