package alexey.tools.server.world;

import com.artemis.Aspect;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.One;

public class WorldUtils {

    private WorldUtils() {}



    public static Aspect.Builder createBuilder(All all, One one, Exclude exclude) {
        Aspect.Builder builder = null;

        if (all != null)
            builder = Aspect.all(all.value());

        if (one != null) {
            if (builder == null)
                builder = Aspect.one(one.value()); else
                builder.one(one.value());
        }

        if (exclude != null) {
            if (builder == null)
                builder = Aspect.exclude(exclude.value()); else
                builder.exclude(exclude.value());
        }

        return builder;
    }
}
