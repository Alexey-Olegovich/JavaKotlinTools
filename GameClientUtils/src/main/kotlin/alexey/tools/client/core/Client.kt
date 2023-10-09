package alexey.tools.client.core

import alexey.tools.common.loaders.ObjectIO
import alexey.tools.common.misc.printTry
import alexey.tools.common.mods.ModLoader
import alexey.tools.server.core.Game
import alexey.tools.server.context.DefaultGameContext
import alexey.tools.server.context.GameContext
import alexey.tools.server.loaders.JsonIO
import alexey.tools.server.misc.obtainVariables
import alexey.tools.server.misc.registerVariablesModule
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx

open class Client(protected var screen: Screen = Screen.NULL,
                  override val context: GameContext = DefaultGameContext()): ApplicationAdapter(), Game<Screen> {



    override fun create() {
        printStopTry { screen.enable() }
    }

    override fun dispose() {
        printTry { screen.close() }
        printTry { context.close() }
    }

    override fun render() {
        printStopTry { screen.run() }
    }

    override fun resize(width: Int, height: Int) {
        printStopTry { screen.resize(width, height) }
    }



    override fun stop() {
        Gdx.app.exit()
    }

    override fun getDeltaTime(): Float {
        return Gdx.graphics.deltaTime
    }

    override fun execute(action: Runnable) {
        Gdx.app.postRunnable(action)
    }

    override fun setProcessor(processor: Screen) {
        screen.disable()
        screen = processor
        screen.enable()
        screen.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun getProcessor(): Screen = screen



    protected inline fun printStopTry(action: () -> Unit) =
        try { action() } catch (e: Throwable) { e.printStackTrace(); stop() }



    companion object {

        const val CONFIG_PATH = "configs/client.json"



        fun configure(gameContext: GameContext): Client {
            return DefaultClient(gameContext)
        }

        fun configure(jsonIO: JsonIO, path: String): Client {
            val variables = jsonIO.obtainVariables(path)
            val context = DefaultGameContext()
            context.register(variables)
            context.register(jsonIO)
            context.register(jsonIO, ObjectIO::class.java)
            context.register(jsonIO.modLoader, ModLoader::class.java)
            return DefaultClient(context)
        }

        fun configure(jsonIO: JsonIO): Client {
            jsonIO.objectMapper.registerVariablesModule()
            val context = DefaultGameContext()
            context.register(jsonIO)
            context.register(jsonIO, ObjectIO::class.java)
            context.register(jsonIO.modLoader, ModLoader::class.java)
            return DefaultClient(context)
        }

        fun configure(modLoader: ModLoader): Client {
            val context = DefaultGameContext()
            context.register(modLoader, ModLoader::class.java)
            return DefaultClient(context)
        }
    }
}