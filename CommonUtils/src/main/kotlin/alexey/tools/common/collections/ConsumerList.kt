package alexey.tools.common.collections

import java.util.function.Consumer

class ConsumerList<T>: ObjectList<T>(), Consumer<T> {
    override fun accept(t: T) {
        add(t)
    }
}