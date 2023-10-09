package alexey.tools.server.world;

import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import java.lang.reflect.Method;

public class ReflectionSubscriptionListener implements EntitySubscription.SubscriptionListener {

    private Method insert;
    private Method remove;
    private final Object target;



    public ReflectionSubscriptionListener(Object target) {
        this.target = target;
    }



    @Override
    public void inserted(final IntBag entities) {
        if (insert == null) return;
        final int[] data = entities.getData();
        for (int i = 0, size = entities.size(); i < size; i++) try {
            insert.invoke(target, data[i]);
        } catch (Throwable e) { throw new RuntimeException(e); }
    }

    @Override
    public void removed(IntBag entities) {
        if (remove == null) return;
        final int[] data = entities.getData();
        for (int i = 0, size = entities.size(); i < size; i++) try {
            remove.invoke(target, data[i]);
        } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public void setInsert(Method insert) {
        if (this.insert != null) throw new IllegalStateException();
        this.insert = insert;
    }

    public void setRemove(Method remove) {
        if (this.remove != null) throw new IllegalStateException();
        this.remove = remove;
    }
}
