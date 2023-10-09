package alexey.tools.common.collections

import alexey.tools.common.misc.getByAngle

interface RotatingArray <T> {
    val data: Array<T>
    fun getByAngle(angle: Float) = data.getByAngle(angle)
    fun getByIndex(index: Int) = data[index]
}