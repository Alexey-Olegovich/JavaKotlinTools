package alexey.tools.common.context

import alexey.tools.common.events.TaskManager

fun Variable.addDecreaseToZeroListener(taskManager: TaskManager) {
    addListener(DecreaseToZeroListener(taskManager, this).also { it.accept(this) })
}

fun Variable.addWhileNotZeroListener(taskManager: TaskManager, action: Runnable) {
    addListener(WhileNotZeroListener(taskManager, this, action).also { it.accept(this) })
}

fun Variable.addWhileZeroListener(taskManager: TaskManager, action: Runnable) {
    addListener(WhileZeroListener(taskManager, this, action).also { it.accept(this) })
}

inline fun buildVariables(action: Variables.() -> Unit) = Variables().apply(action)