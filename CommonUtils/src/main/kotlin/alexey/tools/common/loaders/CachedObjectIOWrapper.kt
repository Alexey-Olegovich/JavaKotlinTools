package alexey.tools.common.loaders

class CachedObjectIOWrapper(private val parent: CachedObjectIO): CachedObjectIO() {

    override fun <T> getGroupOrNull(type: Class<T>): CachedPathGroup<T>? =
        parent.getGroupOrNull(type) ?: super.getGroupOrNull(type)

    override fun <T> setGroup(type: Class<T>, group: CachedPathGroup<T>) {
        val parentGroup = parent.getGroupOrNull(type)
        super.setGroup(type, if (parentGroup == null) group else CachedPathGroupWrapper(parentGroup, group))
    }
}