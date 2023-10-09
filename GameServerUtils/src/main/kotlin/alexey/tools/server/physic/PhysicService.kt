package alexey.tools.server.physic

import alexey.tools.common.concurrent.ThreadAsyncRunnable
import alexey.tools.server.core.Server
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import java.io.Closeable

class PhysicService(gravity: Vector2 = Vector2(0F, 0F),
                    doSleep: Boolean = true,
                    var deltaTime: Float = Server.DELTA_TIME.second,
                    var velocityIterations: Int = 8,
                    var positionIterations: Int = 3): ThreadAsyncRunnable(), Closeable {

    private var world: World = World(gravity, doSleep)



    override fun run() { world.step(deltaTime, velocityIterations, positionIterations); world.clearForces() }

    fun getWorld(): World { await(); return world }

    fun newWorld(gravity: Vector2, doSleep: Boolean) { getWorld().dispose(); world = World(gravity, doSleep) }

    override fun close() { shutdown(); getWorld().dispose() }
}