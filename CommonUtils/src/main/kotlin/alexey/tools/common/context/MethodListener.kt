package alexey.tools.common.context

import java.lang.reflect.Method
import java.util.function.Consumer

class MethodListener(private val target: Any,
                     private val method: Method): Consumer<ImmutableVariable> {

    override fun accept(variable: ImmutableVariable) {
        method.invoke(target, variable.value)
    }
}