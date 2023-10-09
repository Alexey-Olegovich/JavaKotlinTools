package alexey.tools.server.core

import alexey.tools.common.concurrent.AbstractRunnableFuture
import alexey.tools.common.connections.Connection
import java.io.Closeable
import java.util.concurrent.Executor
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

abstract class ClientManager<R, W> (private var executor: Executor?): Closeable {

    private var connection: Future<Connection<R, W>?> = AbstractRunnableFuture.getInstance()
    @Volatile var listener: Connection.Listener<R, W> = Connection.Listener.defaultListener()



    fun create(address: String) {
        val executor = executor ?: return
        stop()
        connection = FutureTask {
            createConnection(listener).apply { initialize(address) }
        }.also { executor.execute(it) }
    }

    fun get(): Connection<R, W> = connection.get() ?: throw NullPointerException()

    fun stop() {
        connection.get()?.apply {
            listener = Connection.Listener.defaultListener()
            shutdown()
        }
    }

    override fun close() {
        if (executor == null) return
        executor = null
        stop()
    }



    protected abstract fun createConnection(listener: Connection.Listener<R, W>): Connection<R, W>
}