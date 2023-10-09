package alexey.tools.server.context

import alexey.tools.common.context.DefaultContext
import alexey.tools.common.context.ImmutableContext
import com.badlogic.gdx.utils.Disposable
import java.util.function.Function

class DefaultGameContext: DefaultContext(), GameContext {
    @Suppress("unchecked_cast")
    override fun <T : Disposable> obtain(sharedObjectType: Class<T>): T =
        data.computeIfAbsent(sharedObjectType, disposableComputeFunction).get() as T



    companion object {
        private val disposableComputeFunction = Function<Class<*>, ImmutableContext.Container<*>> {
            GameRegistrableContext.DisposableContainer(it.getDeclaredConstructor().newInstance() as Disposable)
        }
    }
}