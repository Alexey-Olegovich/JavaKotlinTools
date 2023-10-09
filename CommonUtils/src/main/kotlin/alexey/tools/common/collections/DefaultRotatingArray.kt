package alexey.tools.common.collections

import alexey.tools.common.math.DOUBLE_PI
import alexey.tools.common.misc.getByAngle

open class DefaultRotatingArray <T> (final override val data: Array<T>): RotatingArray<T> {
    open val part = DOUBLE_PI / data.size
    override fun getByAngle(angle: Float) = data.getByAngle(angle, part)
}