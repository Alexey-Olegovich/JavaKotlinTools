package alexey.tools.common.level

interface ContactProcessor<P> {
    fun onContact(state: Byte, first: Int, second: Int, payload: P) {}
}