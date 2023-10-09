package alexey.tools.common.math

import alexey.tools.common.collections.ImmutableIntList
import alexey.tools.common.collections.IntList

interface ImmutableIntMatrix2 {

    val width: Int
    val height: Int



    fun getLeftBorder(): ImmutableIntList {
        val result = IntList(height)
        result.setSize(height)
        for (y in 0 ..< height) result.justSet(y, get(0, y))
        return result
    }

    fun getRightBorder(): ImmutableIntList {
        val result = IntList(height)
        result.setSize(height)
        for (y in 0 ..< height) result.justSet(y, get(width - 1, y))
        return result
    }

    fun getUpBorder(): ImmutableIntList {
        val result = IntList(width)
        result.setSize(width)
        for (x in 0 ..< width) result.justSet(x, get(x, height - 1))
        return result
    }

    fun getDownBorder(): ImmutableIntList {
        val result = IntList(width)
        result.setSize(width)
        for (x in 0 ..< width) result.justSet(x, get(x, 0))
        return result
    }

    fun get(x: Int, y: Int): Int = get(index(x, y))

    fun get(index: Int): Int

    fun index(x: Int, y: Int): Int

    fun getOrDefault(x: Int, y: Int, default: Int) = getOrDefault(index(x, y), default)

    fun getOrDefault(index: Int, default: Int) = get(index)

    fun getOrZero(x: Int, y: Int) = getOrDefault(x, y, 0)



    fun onBorder(x: Int, y: Int) =
        x < 1 || x > width - 2 || y < 1 || y > height - 2

    fun allGreat(fromX: Int, toX: Int, y: Int, value: Int): Boolean {
        val row = width * y
        for (x in fromX .. toX) if (get(row + x) <= value) return false
        return true
    }

    fun oneGreat(fromX: Int, toX: Int, y: Int, value: Int): Boolean {
        val row = width * y
        for (x in fromX .. toX) if (get(row + x) > value) return true
        return false
    }

    fun oneGreat(fromX: Int, toX: Int, fromY: Int, toY: Int, value: Int): Boolean {
        for (y in fromY .. toY) {
            if (y < 0 || y >= height) continue
            val row = width * y
            for (x in fromX .. toX) if (x > -1 && x < width && get(row + x) > value) return true
        }
        return false
    }

    fun oneEquals(fromX: Int, toX: Int, fromY: Int, toY: Int, value: Int, countEdge: Boolean = true): Boolean {
        for (y in fromY .. toY) {
            if (y < 0 || y >= height) {
                if (countEdge) return true else continue
            }
            val row = width * y
            for (x in fromX .. toX) {
                if (x < 0 || x >= width) {
                    if (countEdge) return true else continue
                }
                if (get(row + x) == value) return true
            }
        }
        return false
    }
}