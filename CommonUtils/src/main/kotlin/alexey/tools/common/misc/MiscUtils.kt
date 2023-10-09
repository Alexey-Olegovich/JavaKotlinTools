package alexey.tools.common.misc

import alexey.tools.common.events.EventBus
import alexey.tools.common.mods.ModLoader
import java.io.Closeable

fun Iterable<Closeable>.close() = tryForEach { it.close() }

fun Array<out Closeable>.close() = tryForEach { it.close() }

fun EventBus.register(modLoader: ModLoader,
                      modsClassesName: String = "Mod",
                      initializeMethodName: String = "initialize") {

    val modEntries = modLoader.findObjects(modsClassesName, Any::class.java)
    for (modEntry in modEntries) silentTry {
        modEntry.javaClass
            .getDeclaredMethod(initializeMethodName, EventBus::class.java)
            .invoke(modEntry, this)
    }
}