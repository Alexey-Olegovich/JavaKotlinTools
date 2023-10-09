package alexey.tools.common.mods

import alexey.tools.common.misc.tryClose
import alexey.tools.common.resources.FileResource

fun main(args: Array<out String>) {
    val desktopModLoader = DesktopModLoader()
    try {
        val modLoader = CachedModLoader(desktopModLoader)
        val size = args.size
        var entryPointName = ""
        if (size == 0) entryPointName = modLoader.applyConfig(FileResource("config.ini")) else {
            var i = 0
            var action = -1
            do {
                when (val arg = args[i++]) {
                    "-e" -> {
                        if (i >= size) error("Entry point not specified!")
                        entryPointName = args[i++]
                    }
                    "-c" -> {
                        if (i >= size) error("Config file not specified!")
                        desktopModLoader.resourceFactory.create(args[i++]).tryClose {
                            entryPointName = modLoader.applyConfig(this)
                        }
                    }
                    "-d" -> action = 0
                    "-r" -> action = 1
                    else -> {
                        when (action) {
                            -1 -> error("Action name expected (-e, -d, -r, -c)!")
                            0 -> modLoader.addMods(arg)
                            1 -> modLoader.add(arg)
                        }
                    }
                }
            } while (i < size)
        }
        if (entryPointName.isEmpty()) error("Entry point not specified!")
        val entryPoint = modLoader.findObject(entryPointName, ModdedApplication::class.java)
            ?: throw NullPointerException("Entry point '$entryPointName' not found!")
        entryPoint.run(modLoader)
    } finally {
        desktopModLoader.close()
    }
}