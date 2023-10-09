package alexey.tools.server.loaders

import alexey.tools.common.misc.tryClose
import alexey.tools.common.resources.Resource
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.IdentityMap
import com.esotericsoftware.kryo.util.IntMap
import com.esotericsoftware.kryo.util.ObjectIntMap
import com.esotericsoftware.kryo.util.ObjectMap
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

fun <T> Kryo.readValue(source: Resource, outputClass: Class<T>): T =
    readValue(source.getInputStream(), outputClass)

fun Kryo.writeValue(source: Resource, obj: Any?) =
    writeValue(source.getOutputStream(), obj)

fun <T> Kryo.readValue(source: File, outputClass: Class<T>): T =
    readValue(source.inputStream(), outputClass)

fun Kryo.writeValue(source: File, obj: Any?) =
    writeValue(source.outputStream(), obj)

fun <T> Kryo.readValue(source: URL, outputClass: Class<T>): T =
    readValue(source.openStream(), outputClass)

fun Kryo.writeValue(source: URL, obj: Any?) =
    writeValue(source.openConnection().run {
        doOutput = true
        getOutputStream()
    }, obj)

fun <T> Kryo.readValue(source: InputStream, outputClass: Class<T>): T =
    Input(InflaterInputStream(source)).tryClose { readObject(this, outputClass) }

fun Kryo.writeValue(source: OutputStream, obj: Any?) =
    Output(DeflaterOutputStream(source)).tryClose { writeObject(this, obj) }

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