package alexey.tools.server.level

import alexey.tools.common.collections.IntList
import com.badlogic.gdx.utils.IntIntMap

class RemoteEntityIds {
    private val remoteToLocal = IntIntMap()
    private val localToRemote = IntList()



    fun register(remote: Int, local: Int) {
        remoteToLocal.put(remote, local)
        localToRemote.extendSet(local, remote)
    }

    fun getRemote(local: Int) = localToRemote.getOrDefault(local, -1)

    fun getLocal(remote: Int) = remoteToLocal.get(remote, -1)

    fun removeByRemote(remote: Int) = remoteToLocal.remove(remote, -1)

    fun removeByLocal(local: Int) = remoteToLocal.remove(getRemote(local), -1)



    inline fun getLocal(remote: Int, registerFunction: () -> Int): Int {
        var local = getLocal(remote)
        if (local != -1) return local
        local = registerFunction()
        register(remote, local)
        return local
    }
}