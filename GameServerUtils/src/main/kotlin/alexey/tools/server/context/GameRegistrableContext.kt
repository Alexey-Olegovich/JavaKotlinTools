package alexey.tools.server.context

import alexey.tools.common.context.ImmutableContext
import alexey.tools.common.context.RegistrableContext
import com.badlogic.gdx.utils.Disposable

interface GameRegistrableContext: RegistrableContext {

    fun <T: Disposable> register(sharedObject: T, sharedObjectType: Class<in T> = sharedObject.javaClass) =
        register(DisposableContainer(sharedObject, sharedObjectType))



    class DisposableContainer <T: Disposable>(private val value: T,
                                              private val type: Class<in T> = value.javaClass): ImmutableContext.Container<T> {
        override fun get() = value
        override fun getType() = type
        override fun close() { value.dispose() }
    }
}