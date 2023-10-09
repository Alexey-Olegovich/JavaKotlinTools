package alexey.tools.common.math

import alexey.tools.common.collections.ImmutableIntList
import alexey.tools.common.collections.IntList

class BorderedIntMatrix2(val visibleWidth: Int, val visibleHeight: Int): IntMatrix2(visibleWidth + 2, visibleHeight + 2) {



    override fun setLeftBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (y in 0 ..< visibleHeight) set(0, y + 1, tiles.get(y))
    }

    override fun setRightBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (y in 0 ..< visibleHeight) set(width - 1, y + 1, tiles.get(y))
    }

    override fun setUpBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (x in 0 ..< visibleWidth) set(x + 1, height - 1, tiles.get(x))
    }

    override fun setDownBorder(tiles: ImmutableIntList) {
        if (tiles.isEmpty) return
        for (x in 0 ..< visibleWidth) set(x + 1, 0, tiles.get(x))
    }



    override fun getLeftBorder(): ImmutableIntList {
        val result = IntList(visibleHeight)
        result.setSize(visibleHeight)
        for (y in 0 ..< visibleHeight) result.justSet(y, get(0, y + 1))
        return result
    }

    override fun getRightBorder(): ImmutableIntList {
        val result = IntList(visibleHeight)
        result.setSize(visibleHeight)
        for (y in 0 ..< visibleHeight) result.justSet(y, get(width - 1, y + 1))
        return result
    }

    override fun getUpBorder(): ImmutableIntList {
        val result = IntList(visibleWidth)
        result.setSize(visibleWidth)
        for (x in 0 ..< visibleWidth) result.justSet(x, get(x + 1, height - 1))
        return result
    }

    override fun getDownBorder(): ImmutableIntList {
        val result = IntList(visibleWidth)
        result.setSize(visibleWidth)
        for (x in 0 ..< visibleWidth) result.justSet(x, get(x + 1, 0))
        return result
    }



    override fun index(x: Int, y: Int) = super.index(x + 1, y + 1)
}