package alexey.tools.server.context

import alexey.tools.common.context.Context
import com.badlogic.gdx.utils.Disposable

interface GameContext: Context, GameRegistrableContext {
    fun <T: Disposable> obtain(sharedObjectType: Class<T>): T =
        obtain(sharedObjectType) { sharedObjectType.getDeclaredConstructor().newInstance() }

    fun <T: Disposable> obtain(sharedObjectType: Class<out T>, sharedObjectAsType: Class<T>): T =
        obtain(sharedObjectAsType) { sharedObjectType.getDeclaredConstructor().newInstance() }

    override fun remoteCopy(): GameContext = DefaultGameContext().apply { registerCopy(this) }
}