package alexey.tools.hammer.core

import alexey.tools.common.level.RoomLevelGenerator
import alexey.tools.common.math.ImmutableIntMatrix2
import alexey.tools.hammer.objects.HammerMap
import alexey.tools.hammer.objects.HammerSolid
import java.awt.Rectangle
import java.util.Random
import kotlin.math.*

class RoomMapGenerator(val generator: RoomLevelGenerator = RoomLevelGenerator()) {

    fun generate(scale: Int = 64, config: RoomLevelGenerator.GenerationConfig = createConfig(generator.random)): HammerMap {
        val map = HammerMap()
        val matrix = generator.generate(config)
        val hw = matrix.width / 2
        val hh = matrix.height / 2
        for (y in 0 until matrix.height) {
            for (x in 0 until matrix.width) {
                val type = matrix.get(x, y)

                if (type == 0) continue

                val rectangle = getRectangle(matrix, type, x, y)
                for (ry in y until y + rectangle.height)
                    for (rx in x until x + rectangle.width)
                        matrix.set(rx, ry, 0)

                val sx = (x - hw) * scale
                val sy = (y - hh) * scale
                val ex = sx + rectangle.width * scale
                val ey = sy + rectangle.height * scale

                when (type) {
                    1 -> map.world.put(HammerSolid().apply {
                        set(sx, sy, 0, ex, ey, 256, "BRICK/BRICKWALL003A")
                    })
                    else -> {
                        map.world.put(HammerSolid().apply {
                            set(sx, sy, 0, ex, ey, 16, "concrete/concretefloor001a")
                        })
                        map.world.put(HammerSolid().apply {
                            set(sx, sy, 240, ex, ey, 256, "tools/toolsskybox")
                        })
                    }
                }
            }
        }
        return map
    }

    private fun getRectangle(matrix: ImmutableIntMatrix2, type: Int, x: Int, y: Int): Rectangle {
        var mx = x + 1
        while (matrix.get(mx, y) == type) mx++
        var my = y + 1
        while (all(matrix, type, my, x, mx)) my++
        return Rectangle(x, y, mx - x, my - y)
    }

    private fun all(matrix: ImmutableIntMatrix2, type: Int, y: Int, x1: Int, x2: Int): Boolean {
        for (i in x1 until x2) if (matrix.get(i, y) != type) return false
        return true
    }



    companion object {
        fun createConfig(random: Random,
                         size: Int = 256,
                         delta: Int = 64): RoomLevelGenerator.GenerationConfig {

            val angle = random.nextDouble() * PI * 2
            val length = random.nextInt(delta) + size
            val config = RoomLevelGenerator.GenerationConfig()
            config.toX = (cos(angle) * length).roundToInt()
            config.toY = (sin(angle) * length).roundToInt()
            if (config.toX < 1) {
                config.fromX += 1 - config.toX
                config.toX = 1
            }
            if (config.toY < 1) {
                config.fromY += 1 - config.toY
                config.toY = 1
            }
            config.width = delta + size
            config.height = config.width
            val dx = (config.width - 3 - abs(config.toX - config.fromX)) / 2
            val dy = (config.height - 3 - abs(config.toY - config.fromY)) / 2
            config.fromX += dx
            config.toX += dx
            config.fromY += dy
            config.toY += dy
            config.gapBetweenStructures = 1
            return config
        }
    }
}