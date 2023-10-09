package alexey.tools.common.misc

import alexey.tools.common.math.DOUBLE_PI
import kotlin.math.roundToInt

fun <T> Array<T>.makeFirst(index: Int) {
    if (index == 0) return
    val e = get(index)
    System.arraycopy(this, 0, this, 1, index)
    set(0, e)
}

operator fun <T> Array<T>.minus(element: T): Array<T> = ArrayUtils.minus(this, element)

fun <T> Array<T>.containsReference(element: T): Boolean {
    for (i in indices) if (this[i] === element) return true
    return false
}

fun <T> Array<T>.getByAngle(angle: Float, part: Float = DOUBLE_PI / size): T {
    var index = (angle / part).roundToInt()
    when {
        index >= size -> index %= size
        index < 0 -> {
            index %= size
            if (index != 0) index += size
        }
    }
    return get(index)
}