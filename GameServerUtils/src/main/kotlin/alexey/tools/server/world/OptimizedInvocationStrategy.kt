package alexey.tools.server.world

import com.artemis.BaseSystem
import com.artemis.SystemInvocationStrategy
import com.artemis.utils.Bag

class OptimizedInvocationStrategy: SystemInvocationStrategy() {

    override fun setSystems(systems: Bag<BaseSystem>) {
        this.systems = Bag(BaseSystem::class.java, systems.size())
        val data = systems.data
        for (i in 0 ..< systems.size()) if (!disabled.get(i)) this.systems.add(data[i])
    }

    override fun process() {
        val data = systems.data
        for (i in 0 ..< systems.size()) data[i].process()
        updateEntityStates()
    }
}