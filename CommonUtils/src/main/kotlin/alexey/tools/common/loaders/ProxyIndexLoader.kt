package alexey.tools.common.loaders

import java.util.function.IntFunction

class ProxyIndexLoader <T> (val idsToPaths: List<String>,
                            val group: PathGroup<T>): IntFunction<T?> {

    override fun apply(value: Int): T? {
        return group.readObject(idsToPaths.getOrNull(value) ?: return null)
    }

}