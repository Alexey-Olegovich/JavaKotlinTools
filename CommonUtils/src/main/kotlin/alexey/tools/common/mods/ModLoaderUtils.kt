package alexey.tools.common.mods

import alexey.tools.common.misc.*
import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.readConfig
import java.util.function.BiConsumer

private class ConfigParser(private val modLoader: ModLoader): BiConsumer<String, String> {
    private var entry = ""

    override fun accept(t: String, u: String) {
        when(t) {
            "resources" -> u.split(',').forEach { if (it.isNotEmpty()) modLoader.add(it) }
            "browse" -> modLoader.addMods(u)
            "entry" -> entry = u
        }
    }

    fun getEntry() = entry
}

fun ModLoader.applyConfig(configSource: Resource): String {
    return ConfigParser(this).run { readConfig(configSource); getEntry() }
}

fun ModLoader.addMods(directory: String) {
    listJustEntries(directory,".zip", ".jar").forEach { add("file-zip", it) }
}