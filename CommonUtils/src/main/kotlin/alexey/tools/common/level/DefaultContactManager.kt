package alexey.tools.common.level

import alexey.tools.common.collections.ObjectCollection

open class DefaultContactManager<T>: ContactManager<T>() {

    private var listeners = ObjectCollection<ContactProcessor<T>>(2)



    fun addListener(listener: ContactProcessor<T>) {
        listeners.add(listener)
    }

    fun removeListener(listener: ContactProcessor<T>) {
        listeners.removeReference(listener)
    }

    override fun clearListeners() {
        listeners.clear()
        super.clearListeners()
    }



    override fun onContact(state: Byte, first: Int, second: Int, payload: T) {
        listeners.forEach { it.onContact(state, first, second, payload) }
        super.onContact(state, first, second, payload)
    }
}