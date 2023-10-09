package alexey.tools.common.identity

open class SimpleObjectFactory <T>: ImmutableObjectFactory<T> {

    protected val registered: MutableMap<T, ObjectProperties<T>> = HashMap()



    override fun get(obj: T) = registered[obj]

    open fun obtain(obj: T): ObjectProperties<T> {
        var d = get(obj)
        if (d == null) {
            d = ObjectProperties(obj, registered.size)
            registered[obj] = d
        }
        return d
    }

    override fun size() = registered.size

    open fun clear() = registered.clear()
}