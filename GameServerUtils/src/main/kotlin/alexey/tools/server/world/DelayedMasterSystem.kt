package alexey.tools.server.world

import alexey.tools.server.core.Server

abstract class DelayedMasterSystem: MasterSystem() {

    private var remain = 0F
    var delay = Server.DELTA_TIME.second



    override fun checkProcessing(): Boolean {
        remain -= world.delta
        if (remain > 0F) return false
        remain += delay
        return true
    }
}