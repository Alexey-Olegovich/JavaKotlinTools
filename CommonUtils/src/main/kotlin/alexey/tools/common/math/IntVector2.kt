package alexey.tools.common.math

import kotlin.math.roundToInt
import kotlin.math.sqrt

class IntVector2(override var x: Int = 0,
                 override var y: Int = 0): ImmutableIntVector2 {



    fun copy(): IntVector2 {
        return IntVector2(x, y)
    }

    fun rotate90(left: Boolean = false) {
        val x = this.x
        if (left) {
            this.x = -y
            y = x
        } else {
            this.x = y
            y = -x
        }
    }

    fun rotate180() {
        x = -x
        y = -y
    }

    fun reflectX() {
        x = -x
    }

    fun reflectY() {
        y = -y
    }

    fun set(v: ImmutableIntVector2) {
        x = v.x
        y = v.y
    }

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun add(v: ImmutableIntVector2): IntVector2 {
        x += v.x
        y += v.y
        return this
    }

    fun sub(v: ImmutableIntVector2): IntVector2 {
        x -= v.x
        y -= v.y
        return this
    }

    fun setLength(len: Double) {
        val c = sqrt((x * x + y * y).toDouble())
        val x = x / c
        val y = y / c
        this.x = (x * len).roundToInt()
        this.y = (y * len).roundToInt()
    }

    fun set(x: Float, y: Float, dx: Int, dy: Int) {
        this.x = (x / dx).roundToInt() * dx
        this.y = (y / dy).roundToInt() * dy
    }

    fun set(x: Float, y: Float, dx: Float, dy: Float) {
        setX(x, dx)
        setY(y, dy)
    }

    fun setX(x: Float, dx: Float) {
        this.x = (x / dx).toInt().let { if (x < 0F) it - 1 else it }
    }

    fun setY(y: Float, dy: Float) {
        this.y = (y / dy).toInt().let { if (y < 0F) it - 1 else it }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImmutableIntVector2) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int = 31 * x + y

    override fun toString(): String = "($x, $y)"
}