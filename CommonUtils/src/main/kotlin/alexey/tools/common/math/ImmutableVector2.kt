package alexey.tools.common.math

class ImmutableVector2(val x: Float = 0F,
                       val y: Float = 0F) {

    constructor(v: ImmutableVector2): this(v.x, v.y)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImmutableVector2) return false
        if (x != other.x) return false
        return y == other.y
    }

    override fun hashCode() = 31 * x.hashCode() + y.hashCode()
}