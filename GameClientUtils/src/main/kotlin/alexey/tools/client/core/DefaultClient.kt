package alexey.tools.client.core

import alexey.tools.common.mods.ModLoader
import alexey.tools.server.context.GameContext
import alexey.tools.server.context.obtainVariables
import alexey.tools.server.core.GameSetup
import alexey.tools.server.core.Server

class DefaultClient(context: GameContext): Client(Screen.NULL, context) {
    @Suppress("unchecked_cast")
    override fun create() {
        printStopTry {
            val modLoader = context.getOrNull(ModLoader::class.java) ?: return
            ((modLoader.findObject(context.obtainVariables(CONFIG_PATH, modLoader)
                .get(Server.GAME_SETUP), GameSetup::class.java) ?: return) as GameSetup<Screen>).setup(this)
        }
    }
}