package alexey.tools.common.math

import alexey.tools.common.collections.ImmutableIntList

open class IntMatrix2(final override val width: Int, final override val height: Int): ImmutableIntMatrix2 {

    val data = IntArray(width * height)



    open fun setLeftBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (y in 0 ..< height) set(0, y, tiles.get(y))
    }

    open fun setRightBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (y in 0 ..< height) set(width - 1, y, tiles.get(y))
    }

    open fun setUpBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (x in 0 ..< width) set(x, height - 1, tiles.get(x))
    }

    open fun setDownBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (x in 0 ..< width) set(x, 0, tiles.get(x))
    }

    open fun set(x: Int, y: Int, value: Int) = set(index(x, y), value)

    open fun set(index: Int, value: Int) { data[index] = value }



    override fun get(index: Int): Int = data[index]

    override fun index(x: Int, y: Int) = x + width * y

    override fun getOrDefault(index: Int, default: Int): Int =
        if (index < 0 || index >= data.size) default else data[index]
}