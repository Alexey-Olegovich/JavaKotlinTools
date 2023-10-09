package alexey.tools.common.identity

class ObjectIdentifier <T> : SimpleObjectIdentifier<T>() {

    private val idFactory: IdFactory = IdFactory()



    override fun obtain(o: T): Int {
        var id = registered[o]
        if (id == null) {
            id = idFactory.obtain()
            registered[o] = id
        }
        return id
    }

    fun free(o: T): Int {
        val id = registered.remove(o) ?: return -1
        idFactory.unsafeFree(id)
        return id
    }

    override fun clear() {
        idFactory.clear()
        registered.clear()
    }
}