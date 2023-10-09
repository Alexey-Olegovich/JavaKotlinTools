package alexey.tools.common.loaders

import alexey.tools.common.resources.Resource

class CachedPathGroupWrapper<T>(private val parent: CachedPathGroup<T>,
                                private val main: CachedPathGroup<T>): CachedPathGroup<T> {

    override fun readObject(path: String): T? =
        parent.readObject(path) ?: main.readObject(path)

    override fun writeObject(path: String, obj: T?): Resource? =
        parent.writeObject(path, obj) ?: main.writeObject(path, obj)

    override fun clear() =
        main.clear()

    override fun close() =
        main.close()

    override fun asMap(): Map<String, T?> =
        parent.asMap() + main.asMap()
}