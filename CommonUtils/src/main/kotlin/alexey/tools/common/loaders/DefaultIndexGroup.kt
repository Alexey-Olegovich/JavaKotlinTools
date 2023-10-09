package alexey.tools.common.loaders

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.resources.Resource
import java.util.function.IntFunction

class DefaultIndexGroup <T> (private val loader: IntFunction<T?>): CachedIndexGroup<T> {

    private val data = ObjectList<T?>()



    override fun writeObject(index: Int, obj: T?): Resource? {
        if (obj == null) return null
        data.extendSet(index, obj)
        return Resource.NULL
    }

    override fun readObject(index: Int): T? {
        data.ensureSpace(index)
        var e = data[index]
        if (e != null) return e
        e = loader.apply(index)
        data.justSet(index, e)
        return e
    }

    override fun clear() {
        close()
        data.clear()
    }

    override fun asList(): List<T?> = data
}