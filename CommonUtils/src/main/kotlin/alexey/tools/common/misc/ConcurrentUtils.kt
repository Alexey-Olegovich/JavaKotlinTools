package alexey.tools.common.misc

import java.util.concurrent.*

fun newCachedDaemonExecutor(): ExecutorService =
    Executors.newCachedThreadPool { Thread(it).apply { isDaemon = true } }

fun newDaemonExecutor(threads: Int = Runtime.getRuntime().availableProcessors()): ExecutorService =
    Executors.newFixedThreadPool(threads) { Thread(it).apply { isDaemon = true } }

fun Iterable<Future<*>>.await() = forEach { it.get() }

fun Iterable<Future<*>>.silentAwait() = forEach { silentTry { it.get() } }

fun Iterable<Future<*>>.interruptAwait() =
    forEach { try { it.get() } catch (_: ExecutionException) {} catch (_: CancellationException) {} }

fun MutableIterable<Future<*>>.cancelAndAwait() {
    iterator().run { while (hasNext()) next().let {
        if (it.isDone) remove() else it.cancel(true) } }
    interruptAwait()
}

fun <T> Executor.submitTask(callable: Callable<T>): Future<T> = FutureTask(callable).also { execute(it) }

fun Iterator<Future<*>>.cancel(interrupt: Boolean = true) {
    while (hasNext()) next().cancel(interrupt)
}

fun Iterable<Future<*>>.cancel(interrupt: Boolean = true) {
    forEach { it.cancel(interrupt) }
}