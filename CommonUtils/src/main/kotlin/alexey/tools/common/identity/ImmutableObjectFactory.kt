package alexey.tools.common.identity

interface ImmutableObjectFactory <T> {
    fun get(obj: T): ObjectProperties<T>?
    fun size(): Int
}