package alexey.tools.server.core

import alexey.tools.server.context.GameContext
import java.util.concurrent.Executor

interface Game<T: Processor>: Executor {
    fun setProcessor(processor: T)
    fun getProcessor(): T

    val context: GameContext

    fun stop()
    fun getDeltaTime(): Float
}