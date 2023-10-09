package alexey.tools.common.collections

import java.util.concurrent.Future

class LazyObjectList <T> (c: Int = 8, private var future: Future<out Collection<T>?>? = null): ObjectList<T>(c) {

    override fun iterator(): MutableIterator<T> {
        initialize()
        return super.iterator()
    }

    override fun get(index: Int): T {
        initialize()
        return super.get(index)
    }

    override fun listIterator(): MutableListIterator<T> {
        initialize()
        return super.listIterator()
    }

    override val size: Int
        get() {
            initialize()
            return length()
        }

    override fun isEmpty(): Boolean {
        initialize()
        return super.isEmpty()
    }

    override fun contains(element: T): Boolean {
        initialize()
        return super.contains(element)
    }

    override fun toArray(): Array<Any> {
        initialize()
        return super.toArray()
    }

    override fun <E : Any?> toArray(a: Array<out E>): Array<E> {
        initialize()
        return super.toArray(a)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        initialize()
        return super.containsAll(elements)
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        initialize()
        return super.listIterator(index)
    }

    override fun indexOf(element: T): Int {
        initialize()
        return super.indexOf(element)
    }

    override fun lastIndexOf(element: T): Int {
        initialize()
        return super.lastIndexOf(element)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        initialize()
        return super.subList(fromIndex, toIndex)
    }



    override fun clear() {
        future = null
        super.clear()
    }



    private fun initialize() {
        future?.run { get()?.let { addAll(it) }; future = null }
    }
}