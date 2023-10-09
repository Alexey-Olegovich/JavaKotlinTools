package alexey.tools.client.core

import alexey.tools.server.core.Processor

interface Screen: Processor {
    fun resize(width: Int, height: Int) {}

    companion object {
        val NULL = object : Screen {}
    }
}