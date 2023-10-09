package alexey.tools.server.core

import alexey.tools.common.mods.ModLoader
import alexey.tools.server.context.GameContext
import alexey.tools.server.context.obtainVariables
import java.io.Closeable
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.FutureTask

class ServerManager(private var executor: Executor?): Closeable {

    private var action: ServerTask? = null
    @Volatile var listener: Listener? = null



    fun create(serverContext: GameContext) {
        val executor = executor ?: return
        stop()
        val action = ServerTask(CreateTask(serverContext))
        executor.execute(action)
        this.action = action
    }

    fun stop() {
        val executor = executor ?: return
        var action = action ?: return
        if (!action.isCreate() || action.cancel(true)) return
        val instance = action.get()
        if (instance.getServer() == null) return
        action = ServerTask(StopTask(instance))
        executor.execute(action)
        this.action = action
    }

    override fun close() {
        if (executor == null) return
        executor = null
        val action = action ?: return
        if (!action.isCreate() || action.cancel(true)) return
        action.get().close()
    }



    private interface ServerInstance: Closeable {
        fun getServer(): Server? = null
        fun getThread(): Thread? = null
        override fun close() {}
    }

    private interface ServerCallable: Callable<ServerInstance>, ServerInstance {
        fun stop() {}
        fun isCreate() = false
    }

    private class ServerTask(private val task: ServerCallable): FutureTask<ServerInstance>(task) {
        override fun set(v: ServerInstance) {
            super.set(v)
            if (isCancelled) task.stop()
        }
        fun isCreate() = task.isCreate()
    }

    private inner class CreateTask(private val serverContext: GameContext): ServerCallable {

        private var thread: Thread? = null
        private var server: Server? = null



        override fun getServer() = server

        override fun getThread() = thread

        @Suppress("unchecked_cast")
        override fun call(): ServerInstance {
            try {
                val modLoader = serverContext.get(ModLoader::class.java)
                val variables = serverContext.obtainVariables(Server.CONFIG_PATH, modLoader)
                val server = Server(serverContext, variables.obtain(Server.DELTA_TIME))
                this.server = server
                (modLoader.findObject(variables.get(Server.GAME_SETUP), GameSetup::class.java) as GameSetup<Processor>)
                    .setup(server)
                thread = server.start()
                listener?.onStart(server)
            } catch (e: Throwable) {
                try {
                    listener?.onStartError(server, e)
                } finally {
                    stop()
                }
            }
            return this
        }

        override fun stop() {
            val server = server ?: return
            try {
                close(server)
                listener?.onStop(server)
            } catch (e2: Throwable) {
                listener?.onStopError(server, e2)
            } finally {
                this.server = null
            }
        }

        override fun close() {
            close(server ?: return)
        }

        override fun isCreate() = true



        private fun close(server: Server) {
            val thread = thread
            if (thread == null) {
                server.close()
            } else {
                server.stop()
                thread.join()
            }
        }
    }

    private inner class StopTask(private val instance: ServerInstance): ServerCallable {

        override fun call(): ServerInstance {
            val server = instance.getServer() ?: return instance
            try {
                instance.close()
                listener?.onStop(server)
            } catch (e: Throwable) {
                listener?.onStopError(server, e)
            }
            return instance
        }
    }



    interface Listener {
        fun onStart(server: Server) {}
        fun onStop(server: Server) {}
        fun onStartError(server: Server?, error: Throwable) { error.printStackTrace() }
        fun onStopError(server: Server, error: Throwable) { error.printStackTrace() }
    }
}