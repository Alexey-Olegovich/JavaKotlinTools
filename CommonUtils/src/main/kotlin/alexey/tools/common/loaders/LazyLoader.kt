package alexey.tools.common.loaders

import alexey.tools.common.collections.ObjectList
import java.util.function.IntFunction

class LazyLoader <T> (private val pathGroup: PathGroup<T>): IntFunction<T?> {

    private val bindings = ObjectList<String>()



    fun bind(index: Int, path: String) {
        bindings.extendSet(index, path)
    }

    fun bind(path: String) {
        bindings.add(path)
    }

    fun getBinding(index: Int): String {
        return bindings.getOrDefault(index, "")
    }



    override fun apply(value: Int): T? {
        return pathGroup.readObject(bindings.getOrNull(value) ?: return null)
    }
}