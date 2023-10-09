package alexey.tools.common.resources

import alexey.tools.common.mods.ModLoader
import alexey.tools.common.mods.ModdedApplication

class ResourceHostApplication: ModdedApplication {
    override fun run(modLoader: ModLoader) {
        TCPSharedResource(ModLoaderResource(modLoader)).use {
            it.initialize(25565)
            println("[INFO] Listening on 25565 port!")
            readln()
        }
        println("[INFO] Closing...")
    }
}