package alexey.tools.common.misc

import java.io.Closeable

inline fun printTry(block: () -> Unit) =
    try { block() } catch (e: Throwable) { e.printStackTrace() }

inline fun <T> nullTry(block: () -> T) =
    try { block() } catch (_: Throwable) { null }

inline fun silentTry(block: () -> Unit) =
    try { block() } catch (_: Throwable) { }

inline fun <T: Closeable> T.safeApply(action: T.() -> Unit): T =
    try { action(); this } catch (e: Throwable) { close(); throw e }

inline fun <T: Closeable, E> T.safeRun(action: T.() -> E): E =
    try { action() } catch (e: Throwable) { close(); throw e }

inline fun <T: Closeable, E> T.safeLet(action: (T) -> E): E =
    try { action(this) } catch (e: Throwable) { close(); throw e }

inline fun <T: Closeable> T.safeAlso(action: (T) -> Unit): T =
    try { action(this); this } catch (e: Throwable) { close(); throw e }

inline fun <T : Closeable, R> T.tryClose(block: T.() -> R): R =
    try { block() } finally { close() }

inline fun <T> Iterable<T>.tryForEach(action: (T) -> Unit) {
    var lastError: Throwable? = null
    for (element in this) try {
        action(element)
    } catch (e: Throwable) { lastError = e }
    if (lastError != null) throw lastError
}

inline fun <T> Array<T>.tryForEach(action: (T) -> Unit) {
    var lastError: Throwable? = null
    for (element in this) try {
        action(element)
    } catch (e: Throwable) { lastError = e }
    if (lastError != null) throw lastError
}