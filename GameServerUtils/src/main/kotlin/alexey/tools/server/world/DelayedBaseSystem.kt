package alexey.tools.server.world

import alexey.tools.server.core.Server
import com.artemis.BaseSystem

abstract class DelayedBaseSystem: BaseSystem() {

    private var remain = 0F
    var delay = Server.DELTA_TIME.second



    override fun checkProcessing(): Boolean {
        remain -= world.delta
        if (remain > 0F) return false
        remain += delay
        return true
    }
}