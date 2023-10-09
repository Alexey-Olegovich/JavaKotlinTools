package alexey.tools.server.level

import alexey.tools.common.collections.ImmutableIntList
import alexey.tools.common.collections.ImmutableIntSet
import alexey.tools.common.collections.IntList
import alexey.tools.common.collections.IntSet
import alexey.tools.common.collections.OptimizedIntSet
import alexey.tools.common.math.ImmutableIntMatrix2
import alexey.tools.common.collections.forEachInt
import alexey.tools.server.loaders.getOrPut
import alexey.tools.common.math.BorderedIntMatrix2
import com.esotericsoftware.kryo.util.IntMap
import java.security.SecureRandom
import java.util.Random

class ContinuousTerrainGenerator(val random: Random = SecureRandom()) {

    private val tilePreferences = IntList() // by id
    private val weights = IntList() // by id
    private val preferences = IntMap<OptimizedIntSet>()
    private val mask = IntSet()

    init {
        tilePreferences.unsafeAdd(0)
        weights.unsafeAdd(0)
    }



    fun addTile(preferences: Int, weight: Int = 100) {
        val tileId = tilePreferences.size()

        tilePreferences.add(preferences)
        weights.add(weight)

        addPreferences(preferences.up    shl 8, tileId)
        addPreferences(preferences.down  shr 8, tileId)
        addPreferences(preferences.right shl 8, tileId)
        addPreferences(preferences.left  shr 8, tileId)

        mask.add(tileId)
    }

    fun generateRight(config: GenerationConfig): ImmutableIntMatrix2 {
        val result = prepareGrid(config)
        if (config.reverse)
            for (x in 0 ..< config.width) for (y in config.height - 1 downTo 0)
                result.set(x, y, generateTile(result, x, y)) else
            for (x in 0 ..< config.width) for (y in 0 ..< config.height)
                result.set(x, y, generateTile(result, x, y))
        return result
    }

    fun generateUp(config: GenerationConfig): ImmutableIntMatrix2 {
        val result = prepareGrid(config)
        if (config.reverse)
            for (y in 0 ..< config.height) for (x in config.width - 1 downTo 0)
                result.set(x, y, generateTile(result, x, y)) else
            for (y in 0 ..< config.height) for (x in 0 ..< config.width)
                result.set(x, y, generateTile(result, x, y))
        return result
    }

    fun generateLeft(config: GenerationConfig): ImmutableIntMatrix2 {
        val result = prepareGrid(config)
        if (config.reverse)
            for (x in config.width - 1 downTo 0) for (y in config.height - 1 downTo 0)
                result.set(x, y, generateTile(result, x, y)) else
            for (x in config.width - 1 downTo 0) for (y in 0 ..< config.height)
                result.set(x, y, generateTile(result, x, y))
        return result
    }

    fun generateDown(config: GenerationConfig): ImmutableIntMatrix2 {
        val result = prepareGrid(config)
        if (config.reverse)
            for (y in config.height - 1 downTo 0) for (x in config.width - 1 downTo 0)
                result.set(x, y, generateTile(result, x, y)) else
            for (y in config.height - 1 downTo 0) for (x in 0 ..< config.width)
                result.set(x, y, generateTile(result, x, y))
        return result
    }

    fun generateTile(borderedIntMatrix2: BorderedIntMatrix2, x: Int, y: Int): Int =
        randomTile(getCandidates(
            tilePreferences[borderedIntMatrix2.get(x    , y + 1)].down,
            tilePreferences[borderedIntMatrix2.get(x + 1, y    )].left,
            tilePreferences[borderedIntMatrix2.get(x    , y - 1)].up,
            tilePreferences[borderedIntMatrix2.get(x - 1, y    )].right
        ))

    fun getCandidates(up: Int = 0, right: Int = 0, down: Int = 0, left: Int = 0): ImmutableIntSet {
        val result = IntSet(mask)
        if (up    != 0) result.retain(preferences.get(up   ) ?: return ImmutableIntSet.EMPTY)
        if (right != 0) result.retain(preferences.get(right) ?: return ImmutableIntSet.EMPTY)
        if (down  != 0) result.retain(preferences.get(down ) ?: return ImmutableIntSet.EMPTY)
        if (left  != 0) result.retain(preferences.get(left ) ?: return ImmutableIntSet.EMPTY)
        return result
    }

    fun randomTile(candidates: ImmutableIntSet): Int {
        if (candidates.isEmpty) return 0
        var sum = 0
        candidates.forEachInt { sum += weights[it] }
        var randomWeight = random.nextInt(sum)
        candidates.forEachInt {
            randomWeight -= weights[it]
            if (randomWeight < 0) return it
        }
        return 0
    }



    private fun prepareGrid(config: GenerationConfig): BorderedIntMatrix2 {
        val result = BorderedIntMatrix2(config.width, config.height)
        result.setDownBorder(config.down)
        result.setUpBorder(config.up)
        result.setLeftBorder(config.left)
        result.setRightBorder(config.right)
        return result
    }

    private fun addPreferences(preferences: Int, tileId: Int) {
        this.preferences.getOrPut(preferences) { OptimizedIntSet() }.add(tileId)
    }

    private val Int.down  : Int get() = this.and(0x7F000000)
    private val Int.up    : Int get() = this.and(0x00FF0000)
    private val Int.left  : Int get() = this.and(0x0000FF00)
    private val Int.right : Int get() = this.and(0x000000FF)



    class GenerationConfig(var width: Int = 16,
                           var height: Int = 16,
                           var reverse: Boolean = false,
                           var down: ImmutableIntList = ImmutableIntList.EMPTY,
                           var up: ImmutableIntList = ImmutableIntList.EMPTY,
                           var left: ImmutableIntList = ImmutableIntList.EMPTY,
                           var right: ImmutableIntList = ImmutableIntList.EMPTY)
}