package alexey.tools.server.core

import alexey.tools.common.mods.ModLoader
import alexey.tools.common.mods.ModdedApplication

class ServerApplication: ModdedApplication {

    override fun run(modLoader: ModLoader) {
        Server.configure(modLoader).start().join()
    }
}