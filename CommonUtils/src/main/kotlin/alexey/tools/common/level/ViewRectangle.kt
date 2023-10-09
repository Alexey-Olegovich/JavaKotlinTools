package alexey.tools.common.level

import alexey.tools.common.math.ImmutableIntVector2

class ViewRectangle(c: ImmutableIntVector2, r: Int) {

    val dx = c.x - r .. c.x + r
    val dy = c.y - r .. c.y + r

    fun contains(x: Int, y: Int) = dx.contains(x) && dy.contains(y)

    fun containsBorder(x: Int, y: Int) =
        (dx.contains(x) && (y == dy.first || y == dy.last)) ||
        (dy.contains(y) && (x == dx.first || x == dx.last))

    inline fun forEach(action: (Int, Int) -> Unit) {
        for (x in dx) for (y in dy) action(x, y)
    }

    inline fun forEach(except: ViewRectangle, action: (Int, Int) -> Unit) {
        forEach { x, y -> if (!except.contains(x, y)) action(x, y) }
    }

    inline fun forEachBorder(action: (Int, Int) -> Unit) {
        for (x in dx) {
            action(x, dy.first)
            action(x, dy.last)
        }
        for (y in dy.first + 1 ..< dy.last) {
            action(dx.first, y)
            action(dx.last, y)
        }
    }

    inline fun forEachBorder(except: ViewRectangle, action: (Int, Int) -> Unit) {
        forEachBorder { x, y -> if (!except.containsBorder(x, y)) action(x, y) }
    }
}