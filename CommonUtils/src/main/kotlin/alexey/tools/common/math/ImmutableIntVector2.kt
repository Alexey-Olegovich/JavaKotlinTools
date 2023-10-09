package alexey.tools.common.math

import kotlin.math.sqrt

interface ImmutableIntVector2 {
    val x: Int
    val y: Int

    fun length() = sqrt((x * x + y * y).toDouble())

    fun equals(x: Int, y: Int) =
        this.x == x && this.y == y

    companion object {
        val ZERO = object : ImmutableIntVector2 {
            override val x = 0
            override val y = 0

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is ImmutableIntVector2) return false
                return x == other.x && y == other.y
            }

            override fun hashCode(): Int = 0

            override fun toString(): String = "(0, 0)"
        }
    }
}