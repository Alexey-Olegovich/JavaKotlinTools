package alexey.tools.server.core

import alexey.tools.common.loaders.ObjectIO
import alexey.tools.common.collections.run
import alexey.tools.common.mods.ModLoader
import alexey.tools.server.context.DefaultGameContext
import alexey.tools.server.context.GameContext
import alexey.tools.server.context.obtainVariables
import alexey.tools.server.loaders.JsonIO
import alexey.tools.server.misc.obtainVariables
import java.io.Closeable
import java.util.concurrent.ConcurrentLinkedQueue

class Server(override val context: GameContext = DefaultGameContext(),
             private var deltaTime: Float = DELTA_TIME.second): Game<Processor>, Runnable, Closeable {

    private var processor = Processor.NULL
    @Volatile private var shouldRun = true
    private var step = (deltaTime * 1_000_000_000L).toLong()
    private var lastTime = System.nanoTime()
    private var timeSinceLastUpdate = 0L
    private val commands = ConcurrentLinkedQueue<Runnable>()



    override fun setProcessor(processor: Processor) {
        this.processor.disable()
        this.processor = processor
        this.processor.enable()
    }

    override fun getProcessor(): Processor = processor

    override fun stop() {
        shouldRun = false
    }

    override fun getDeltaTime(): Float = deltaTime

    override fun run() {
        lastTime = System.nanoTime()
        try {
            while (shouldRun) update()
        } finally {
            close()
        }
        shouldRun = true
    }

    override fun execute(action: Runnable) {
        commands.add(action)
    }

    override fun close() {
        try { processor.close() } finally { context.close() }
    }

    fun start(): Thread = Thread(this).apply { start() }

    fun setStep(step: Float) {
        deltaTime = step
        this.step = (deltaTime * 1_000_000_000L).toLong()
    }

    fun update() {
        lastTime = System.nanoTime().also { timeSinceLastUpdate += it - lastTime }
        if (timeSinceLastUpdate < step) {
            val remain = step - timeSinceLastUpdate
            if (remain >= 1_000_000L) Thread.sleep(remain / 1_000_000L) // Reduce CPU usage...
        } else {
            do {
                commands.run()
                processor.run()
                timeSinceLastUpdate -= step
            } while (timeSinceLastUpdate >= step)
        }
    }



    companion object {

        @Suppress("unchecked_cast")
        fun configure(jsonIO: JsonIO, path: String = CONFIG_PATH): Server {
            val variables = jsonIO.obtainVariables(path)
            val context = DefaultGameContext()
            context.register(variables)
            context.register(jsonIO)
            context.register(jsonIO, ObjectIO::class.java)
            context.register(jsonIO.modLoader, ModLoader::class.java)
            val server = Server(context, variables.obtain(DELTA_TIME))
            val gameSetup = jsonIO.modLoader.findObject(variables.get(GAME_SETUP), GameSetup::class.java) ?: return server
            (gameSetup as GameSetup<Processor>).setup(server)
            return server
        }

        fun configure(modLoader: ModLoader, configPath: String = CONFIG_PATH): Server {
            return configure(JsonIO(modLoader), configPath)
        }

        @Suppress("unchecked_cast")
        fun configure(gameContext: GameContext): Server {
            val modLoader = gameContext.get(ModLoader::class.java)
            val variables = gameContext.obtainVariables(CONFIG_PATH, modLoader)
            val server = Server(gameContext, variables.obtain(DELTA_TIME))
            (modLoader.findObject(variables.get(GAME_SETUP), GameSetup::class.java) as GameSetup<Processor>)
                .setup(server)
            return server
        }



        const val CONFIG_PATH = "configs/server.json"
        val DELTA_TIME = Pair("deltaTime", 1F / 60F)
        val GAME_SETUP = Pair("gameSetup", "")
    }
}