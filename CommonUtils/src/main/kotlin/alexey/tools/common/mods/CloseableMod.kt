package alexey.tools.common.mods

import alexey.tools.common.resources.ResourceRoot
import java.io.Closeable

interface CloseableMod: Mod, Closeable {
    override fun getResource(): ResourceRoot
    override fun close() { getResource().close() }
}