package alexey.tools.common.collections

import alexey.tools.common.math.DOUBLE_PI
import kotlin.math.roundToInt

class MirroredRotatingArray <T> (data: Array<T>): DefaultRotatingArray<T>(data) {
    val full = data.size + data.size - 2
    override val part = DOUBLE_PI / full
    override fun getByAngle(angle: Float): T {
        var index = (angle / part).roundToInt()
        if (index < 0) {
            index = -(index % full)
            if (index >= data.size) index = data.size - (index % data.size) - 2
        } else if (index >= data.size) {
            index %= full
            if (index >= data.size) index = data.size - (index % data.size) - 2
        }
        return data[index]
    }
}