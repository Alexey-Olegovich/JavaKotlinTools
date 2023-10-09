package alexey.tools.common.level

open class ContactManager <P>: EntityListeners<ContactProcessor<P>>(), ContactProcessor<P> {

    override fun onContact(state: Byte, first: Int, second: Int, payload: P) {
        if (first != -1) getListeners(first)?.forEach { it.onContact(state, first, second, payload) }
        if (second != -1) getListeners(second)?.forEach { it.onContact(state, second, first, payload) }
    }
}