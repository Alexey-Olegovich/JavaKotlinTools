package alexey.tools.server.core

open class ProcessorWrapper <T: Processor> (val context: T): Processor {
    override fun run() {
        context.run()
    }

    override fun enable() {
        context.enable()
    }

    override fun disable() {
        context.disable()
    }

    override fun close() {
        context.close()
    }
}