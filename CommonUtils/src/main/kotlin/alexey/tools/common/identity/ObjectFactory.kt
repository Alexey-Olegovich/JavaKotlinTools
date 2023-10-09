package alexey.tools.common.identity

class ObjectFactory <T> : SimpleObjectFactory<T>() {

    private val idFactory: IdFactory = IdFactory()



    override fun obtain(obj: T): ObjectProperties<T> {
        var d = get(obj)
        if (d == null) {
            d = ObjectProperties(obj, idFactory.obtain())
            registered[obj] = d
        }
        return d
    }

    fun free(obj: T): ObjectProperties<T>? {
        val od = registered.remove(obj)
        if (od != null) idFactory.unsafeFree(od.id)
        return od
    }

    override fun clear() {
        idFactory.clear()
        registered.clear()
    }
}