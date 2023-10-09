package alexey.tools.client.core

open class ScreenWrapper <T: Screen> (val context: T): Screen {
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

    override fun resize(width: Int, height: Int) {
        context.resize(width, height)
    }
}