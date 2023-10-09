package alexey.tools.common.collections

import alexey.tools.common.misc.silentTry
import java.util.Queue

inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    for (i in indices) action(get(i))
}

inline fun <T> List<T>.silentFastForEach(action: (T) -> Unit) {
    for (i in indices) silentTry { action(get(i)) }
}

inline fun <T> ObjectStorage<T>.getOrExtendSet(index: Int, defaultValue: () -> T): T {
    ensureSpace(index)
    var e = get(index)
    if (e != null) return e
    e = defaultValue()
    justSet(index, e)
    return e
}

inline fun <T> MutableIterable<T>.removeFirst(predicate: (T) -> Boolean): T? {
    val i = iterator()
    while (i.hasNext()) {
        val result = i.next()
        if (predicate(result)) { i.remove(); return result }
    }
    return null
}

inline fun <T> CompactObjectStorage<T>.getOrGrowSet(index: Int, default: () -> T): T {
    growIndex(index)
    var e = get(index)
    if (e != null) return e
    e = default()
    justSet(index, e)
    return e
}

inline fun <T> CompactObjectStorage<T>.getOrExtendSet(index: Int, default: () -> T): T {
    var e: T
    if (index < size) {
        e = get(index)
        if (e == null) { e = default(); justSet(index, e) }
    } else {
        e = default()
        setCapacity(index + 1)
        justSet(index, e)
    }
    return e
}

fun <K> Map<K, Any>.getBoolean(key: K, defaultValue: Boolean = false): Boolean {
    return (get(key) ?: return defaultValue) as Boolean
}

fun <K> Map<K, Any>.getString(key: K, defaultValue: String = ""): String {
    return (get(key) ?: return defaultValue) as String
}

fun <K> Map<K, Any>.getNumber(key: K): Number? {
    return get(key) as Number?
}

fun <K> Map<K, Any>.getShort(key: K, defaultValue: Short = 0): Short {
    return (getNumber(key) ?: return defaultValue).toShort()
}

fun <K> Map<K, Any>.getInt(key: K, defaultValue: Int = 0): Int {
    return (getNumber(key) ?: return defaultValue).toInt()
}

fun <K> Map<K, Any>.getFloat(key: K, defaultValue: Float = 0F): Float {
    return (getNumber(key) ?: return defaultValue).toFloat()
}

fun <K> Map<K, Any>.getDouble(key: K, defaultValue: Double = 0.0): Double {
    return (getNumber(key) ?: return defaultValue).toDouble()
}

fun <K> Map<K, Any>.getByte(key: K, defaultValue: Byte = 0): Byte {
    return (getNumber(key) ?: return defaultValue).toByte()
}

inline fun <T, E> Collection<T>.safeConvert(f: (T) -> E?): List<E> =
    if (isEmpty()) emptyList() else ObjectList<E>(size)
        .also { r -> forEach { f(it).let { e -> if (e != null) r.unsafeAdd(e) } } }

inline fun <T, E> Collection<T>.convert(f: (T) -> E): List<E> =
    if (isEmpty()) emptyList() else ObjectList<E>(size).also { r -> forEach { r.unsafeAdd(f(it)) } }

inline fun <T, E> Array<T>.convert(f: (T) -> E): List<E> =
    if (isEmpty()) emptyList() else ObjectList<E>(size).also { r -> forEach { r.unsafeAdd(f(it)) } }

inline fun IntIterable.forEachInt(action: (Int) -> Unit) {
    val i = intIterator()
    while (i.hasNext()) action(i.nextInt())
}

inline fun <T> Queue<T>.collect(action: (T) -> Unit) {
    var e = poll()
    while (e != null) {
        action(e)
        e = poll()
    }
}

fun Queue<Runnable>.run() = collect { it.run() }

fun <V: Any> MutableMap<String, V>.set(value: V) = put(value.javaClass.name, value)

fun <V: Any, V2: V> MutableMap<String, V>.set(value: V2, type: Class<V2>) = put(type.name, value)

@Suppress("unchecked_cast")
fun <V: Any, V2: V> Map<String, V>.get(type: Class<V2>) = get(type.name) as V2?

fun <V: Any, V2: V> Map<String, V>.get(type: Class<V2>, default: V2) = get(type) ?: default

fun <V: Any, V2: V> Map<String, V>.obtain(type: Class<V2>) = get(type) ?: throw NullPointerException()

fun List<*>.containsReference(v: Any): Boolean {
    forEach { if (it === v) return true }
    return false
}

fun <T> Iterator<T>.toList(): List<T> = ConsumerList<T>().also { forEachRemaining(it) }