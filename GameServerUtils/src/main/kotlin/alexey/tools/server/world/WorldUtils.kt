package alexey.tools.server.world

import alexey.tools.common.collections.CompactObjectStorage
import alexey.tools.common.collections.getOrExtendSet
import com.artemis.Aspect
import com.artemis.AspectSubscriptionManager
import com.artemis.World
import com.artemis.annotations.All
import com.artemis.annotations.Exclude
import com.artemis.annotations.One
import com.artemis.utils.IntBag
import java.lang.reflect.Method

fun <T> World.getSharedObject(type: Class<T>): T =
    getRegistered(type) ?: error(type.name)

fun <T> World.getSharedObject(name: String): T =
    getRegistered(name) ?: error(name)

private fun error(name: String): Nothing = throw NullPointerException("Shared object ($name) not found!")

inline fun IntBag.forEach(action: (Int) -> Unit) {
    val d = data
    for (i in 0 ..< size()) action(d[i])
}

val Method.aspectBuilder: Aspect.Builder?
    get() = WorldUtils.createBuilder(getDeclaredAnnotation(All::class.java),
        getDeclaredAnnotation(One::class.java),
        getDeclaredAnnotation(Exclude::class.java))

val Class<*>.aspectBuilder: Aspect.Builder?
    get() = WorldUtils.createBuilder(getDeclaredAnnotation(All::class.java),
        getDeclaredAnnotation(One::class.java),
        getDeclaredAnnotation(Exclude::class.java))

fun AspectSubscriptionManager.addSubscriptionListeners(target: Any) {
    val temp = CompactObjectStorage<ReflectionSubscriptionListener>()
    for (method in target.javaClass.declaredMethods) {
        val insert = method.getDeclaredAnnotation(Insert::class.java)
        if (insert != null) {
            method.isAccessible = true
            val listener = temp.getOrExtendSet(insert.index) { ReflectionSubscriptionListener(target) }
            listener.setInsert(method)
            get(method.aspectBuilder ?: continue).addSubscriptionListener(listener)
        } else {
            val remove = method.getDeclaredAnnotation(Remove::class.java) ?: continue
            method.isAccessible = true
            val listener = temp.getOrExtendSet(remove.index) { ReflectionSubscriptionListener(target) }
            listener.setRemove(method)
            get(method.aspectBuilder ?: continue).addSubscriptionListener(listener)
        }
    }
}