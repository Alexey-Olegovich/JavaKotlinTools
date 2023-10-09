package alexey.tools.common.level

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.math.*
import java.security.SecureRandom
import java.util.Random

class RoomLevelGenerator(val random: Random = SecureRandom()) {

    private val structures = ObjectList<StructureData>()
    private var weightSum = 0



    fun addStructure(width: Int, height: Int, weight: Int = 100) {
        structures.add(StructureData(structures.size + 3, width, height, weight))
        weightSum += weight
    }

    fun randomStructureData(): StructureData {
        var randomWeight = random.nextInt(weightSum)
        structures.forEach {
            randomWeight -= it.weight
            if (randomWeight < 0) return it
        }
        throw IllegalStateException()
    }

    fun getStructureDataList(): List<StructureData> = structures

    fun generate(config: GenerationConfig): IntMatrix2 {
        val grid = IntMatrix2(config.width, config.height)
        val walkers = ObjectList<GridWalker>()

        val from = IntVector2(config.fromX, config.fromY)
        val to = IntVector2(config.toX, config.toY)
        walkers.add(GridWalker(grid, from, to))

        val mainDirection = to.copy().sub(from)

        while (walkers.isNotEmpty) {
            val mainWalker = walkers[0]
            if (mainWalker.hp < 0 && random.preciseRoll(config.extraPathChance)) {
                val goal = mainDirection.copy()
                goal.rotate90(random.nextBoolean())
                goal.setLength(config.extraPathLength.toDouble())
                goal.add(mainWalker.position)
                walkers.add(GridWalker(grid, mainWalker.position.copy(), goal, config.extraPathLength))
            }
            val i = walkers.iterator()
            do {
                val walker = i.next()
                grid.set(walker.position.x, walker.position.y, FLOOR)
                if (!walker.update()) i.remove()
            } while (i.hasNext())
        }

        if (structures.isNotEmpty) {
            val gridStructures = ObjectList<Structure>()
            var currentStructure: Structure? = null

            for (x in 0..<grid.width) for (y in 0..<grid.height) {
                if (grid.get(x, y) != FLOOR) continue

                if (currentStructure == null) currentStructure = Structure(0, 0, randomStructureData())

                currentStructure.x = x - currentStructure.data.width / 2
                currentStructure.y = y - currentStructure.data.height / 2

                val topX = currentStructure.topX()
                val topY = currentStructure.topY()

                if (currentStructure.x < 1 || currentStructure.y < 1 ||
                    topX > grid.width - 1 || topY > grid.height - 1) continue

                if (currentStructure.overlap(gridStructures,
                        config.gapBetweenStructures, config.gapBetweenStructures)) continue

                gridStructures.add(currentStructure)

                for (sx in currentStructure.x..<topX)
                    for (sy in currentStructure.y..<topY)
                        grid.set(sx, sy, FLOOR)
                grid.set(currentStructure.x, topY - 1, currentStructure.data.index)

                currentStructure = null
            }
        }

        for (x in 2 ..< grid.width - 2) for (y in 2 ..< grid.height - 2) if (grid.get(x, y) > WALL) {
            val l = grid.index(x - 1, y)
            val r = grid.index(x + 1, y)
            val u = grid.index(x, y + 1)
            val d = grid.index(x, y - 1)
            val lv = grid.get(l)
            val rv = grid.get(r)
            val uv = grid.get(u)
            val dv = grid.get(d)
            if (lv == VOID && rv == VOID &&
                uv > WALL && dv > WALL) {
                grid.set(l, FLOOR)
                grid.set(r, FLOOR)
            } else if (uv == VOID && dv == VOID &&
                lv > WALL && rv > WALL) {
                grid.set(u, FLOOR)
                grid.set(d, FLOOR)
            }
        }

        for (x in 0 ..< grid.width) for (y in 0 ..< grid.height)
            if (grid.get(x, y) == VOID && grid.oneGreat(x - 1, x + 1, y - 1, y + 1, WALL))
                grid.set(x, y, WALL)

        for (x in 0 ..< grid.width) for (y in 0 ..< grid.height)
            if (grid.get(x, y) == WALL && !grid.oneEquals(x - 1, x + 1, y - 1, y + 1, VOID))
                grid.set(x, y, FLOOR)

        return grid
    }



    private open inner class GridWalker(val grid: ImmutableIntMatrix2, position: IntVector2,
                                        val to: ImmutableIntVector2, var hp: Int = -1): Walker(position) {

        open fun update(): Boolean {
            if (hp == 0) return false

            if (isLookingAt(to.x, to.y)) {
                if (random.roll(20)) velocity.rotate90(random.nextBoolean())
            } else {
                when {
                    random.roll(18) -> velocity.rotate180()
                    random.roll(12) -> velocity.rotate90(random.nextBoolean())
                }
            }

            walkForward()

            if (position.x < 1) position.x = 1 else if (position.x > grid.width  - 2) position.x = grid.width  - 2
            if (position.y < 1) position.y = 1 else if (position.y > grid.height - 2) position.y = grid.height - 2
            if (to.x - position.x == 0 && to.y - position.y == 0) hp = 0 else hp -= 1

            return true
        }
    }



    class GenerationConfig(var width: Int = 16, var height: Int = 16,
                           var fromX: Int = 1, var fromY: Int = 1,
                           var toX: Int = 0, var toY: Int = 0,
                           var extraPathLength: Int = 8,
                           var extraPathChance: Double = 0.01,
                           var gapBetweenStructures: Int = 1)

    class StructureData(val index: Int, val width: Int, val height: Int, val weight: Int = 100)

    class Structure(var x: Int, var y: Int, val data: StructureData) {

        fun overlap(structure: Structure, xGap: Int = 0, yGap: Int = 0): Boolean =
            x < structure.topX() + xGap && topX() > structure.x - xGap && y < structure.topY() + yGap && topY() > structure.y - yGap

        fun overlap(structures: List<Structure>, xGap: Int = 0, yGap: Int = 0): Boolean {
            structures.forEach { if (overlap(it, xGap, yGap)) return true }
            return false
        }

        fun topX() = x + data.width

        fun topY() = y + data.height
    }



    companion object {
        const val VOID = 0
        const val WALL = 1
        const val FLOOR = 2
    }
}