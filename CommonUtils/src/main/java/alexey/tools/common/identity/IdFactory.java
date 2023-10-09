package alexey.tools.common.identity;

import alexey.tools.common.collections.IntList;

public class IdFactory {
    protected final IntList used = new IntList(4);
    protected int nextId = 0;

    public int obtain() {
        return used.isEmpty() ? nextId++ : used.removeLast();
    }

    public void free(int id) {
        if (id < nextId && !used.contains(id)) used.add(id);
    }

    public void unsafeFree(int id) {
        used.add(id);
    }

    public int size() {
        return nextId;
    }

    public void clear() {
        nextId = 0;
        used.clear();
    }
}
