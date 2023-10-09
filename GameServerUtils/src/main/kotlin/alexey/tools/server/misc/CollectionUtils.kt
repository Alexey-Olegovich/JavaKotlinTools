package alexey.tools.server.misc

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.*

inline fun <K, V> ObjectMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    var value = get(key)
    if (value == null) {
        value = defaultValue()
        put(key, value)
    }
    return value
}

fun <K> ObjectIntMap<K>.getOrPut(key: K, default: Int, defaultValue: Int): Int {
    var value = get(key, default)
    if (value == default) {
        value = defaultValue
        put(key, value)
    }
    return value
}

fun IntIntMap.getOrPut(key: Int, default: Int, defaultValue: Int): Int {
    var value = get(key, default)
    if (value == default) {
        value = defaultValue
        put(key, value)
    }
    return value
}

inline fun IntIntMap.getOrPut(key: Int, default: Int, defaultValue: () -> Int): Int {
    var value = get(key, default)
    if (value == default) {
        value = defaultValue()
        put(key, value)
    }
    return value
}

inline fun <K, V> IdentityMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    var value = get(key)
    if (value == null) {
        value = defaultValue()
        put(key, value)
    }
    return value
}

inline fun <V> IntMap<V>.getOrPut(key: Int, defaultValue: () -> V): V {
    var value = get(key)
    if (value == null) {
        value = defaultValue()
        put(key, value)
    }
    return value
}

inline fun IntSet.forEach(action: (Int) -> Unit) {
    val i = iterator()
    while (i.hasNext) action(i.next())
}

val EMPTY_VECTOR2_ARRAY = emptyArray<Vector2>()