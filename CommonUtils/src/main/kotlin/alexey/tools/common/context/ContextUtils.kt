package alexey.tools.common.context

import alexey.tools.common.events.EventBus
import alexey.tools.common.events.EventBusWrapper
import alexey.tools.common.events.NetEventBus
import alexey.tools.common.events.NetEventBusWrapper
import alexey.tools.common.loaders.CachedObjectIO
import alexey.tools.common.loaders.CachedObjectIOWrapper
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun RegistrableContext.registerExecutorService(backgroundExecutor: ExecutorService) =
    register(backgroundExecutor, ExecutorService::class.java, ExecutorService::shutdown)

fun RegistrableContext.registerExecutorService(threads: Int): ExecutorService =
    Executors.newFixedThreadPool(threads).also { registerExecutorService(it) }

fun RegistrableContext.registerEventBus() =
    EventBus().also { register(it) }

fun RegistrableContext.registerCachedObjectIO(context: ImmutableContext) =
    context.getOrNull(CachedObjectIO::class.java)
        .let { if (it == null) CachedObjectIO() else CachedObjectIOWrapper(it) }
        .also { register(it, CachedObjectIO::class.java) }

fun RegistrableContext.registerEventBus(context: ImmutableContext) =
    context.getOrNull(EventBus::class.java)
        .let { if (it == null) EventBus() else EventBusWrapper(it) }
        .also { register(it, EventBus::class.java) }

fun RegistrableContext.registerNetEventBus(context: ImmutableContext) =
    context.getOrNull(NetEventBus::class.java)
        .let { if (it == null) NetEventBus() else NetEventBusWrapper(it) }
        .also { register(it, NetEventBus::class.java) }

inline fun <T: Closeable> Context.obtain(sharedObjectType: Class<T>,
                                         default: () -> T): T {
    var sharedObject = getOrNull(sharedObjectType)
    if (sharedObject != null) return sharedObject
    sharedObject = default()
    register(sharedObject, sharedObjectType)
    return sharedObject
}

inline fun <T: Any> Context.obtain(sharedObjectType: Class<T>,
                                   default: () -> T): T {
    var sharedObject = getOrNull(sharedObjectType)
    if (sharedObject != null) return sharedObject
    sharedObject = default()
    register(sharedObject, sharedObjectType)
    return sharedObject
}

inline fun buildContext(action: Context.() -> Unit): Context = DefaultContext().apply(action)

inline fun buildContext(parent: ImmutableContext, action: Context.() -> Unit): Context {
    val context = DefaultContext()
    context.registerCopy(parent)
    context.apply(action)
    return context
}
