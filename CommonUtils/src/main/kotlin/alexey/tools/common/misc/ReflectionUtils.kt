package alexey.tools.common.misc

import java.lang.reflect.Method

fun Class<*>.hasAnyMethod(methods: Array<out Method>): Boolean {
    val declaredMethods = declaredMethods
    return methods.any { declaredMethods.hasMethod(it) }
}

fun Class<*>.hasMethod(method: Method): Boolean =
    declaredMethods.hasMethod(method)

fun Class<*>.hasAnyMethod(methods: Array<out Method>, until: Class<*>): Boolean {
    var c = this
    while (c != until) {
        if (hasAnyMethod(methods)) return true
        c = c.superclass
    }
    return false
}

fun Class<*>.hasMethod(method: Method, until: Class<*>): Boolean {
    var c = this
    while (c != until) {
        if (hasMethod(method)) return true
        c = c.superclass
    }
    return false
}

fun Array<out Method>.hasMethod(other: Method): Boolean =
    any { it.same(other) }

fun Method.same(other: Method): Boolean =
    name == other.name && parameterTypes.contentEquals(other.parameterTypes)