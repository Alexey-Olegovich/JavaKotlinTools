package alexey.tools.common.identity

open class SimpleObjectIdentifier <T> {

    protected val registered: MutableMap<T, Int> = HashMap()



    open fun obtain(o: T): Int {
        var id = registered[o]
        if (id == null) {
            id = registered.size
            registered[o] = id
        }
        return id
    }

    fun getMap(): Map<T, Int> = registered

    fun get(o: T) = registered[o] ?: -1

    fun contains(o: T) = registered.containsKey(o)

    fun size() = registered.size

    open fun clear() = registered.clear()
}