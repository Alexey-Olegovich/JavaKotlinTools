package alexey.tools.common.context

import java.util.function.Consumer

open class BasicVariable: Variable {

    private var listeners: Entry? = null



    override fun addListener(listener: Consumer<ImmutableVariable>) {
        listeners = Entry(listener, listeners)
    }

    override fun set(value: Any) {
        when (value) {
            is Boolean -> set(value)
            is Number -> set(value)
            else -> set(value.toString())
        }
    }

    protected fun notifyChange() {
        var listener = listeners
        while (listener != null) {
            listener.listener.accept(this)
            listener = listener.next
        }
    }



    private class Entry(val listener: Consumer<ImmutableVariable>,
                        var next: Entry? = null)
}