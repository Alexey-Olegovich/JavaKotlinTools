package alexey.tools.common.concurrent;

import alexey.tools.common.collections.ObjectStorage;

public class SyncTaskDeque implements Runnable {

    private ObjectStorage<Runnable> q1 = new ObjectStorage<>();
    private ObjectStorage<Runnable> q2 = new ObjectStorage<>();
    private final Object lock = new Object();


    // Can be used by multiple threads
    public void add(Runnable o) {
        synchronized (lock) { q1.add(o); }
    }
    // Only one thread must call this method!
    @Override
    public void run() {
        synchronized (lock) {
            ObjectStorage<Runnable> temp = q1;
            q1 = q2;
            q2 = temp;
        }
        while (q2.isNotEmpty()) q2.removeLast().run();
    }
}
