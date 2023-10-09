package alexey.tools.common.level

import alexey.tools.common.math.ImmutableIntVector2
import alexey.tools.common.math.IntVector2
import kotlin.math.sign

open class Walker(val position: IntVector2 = IntVector2(0, 0),
                  val velocity: IntVector2 = IntVector2(1, 0)) {

    fun walkForward() {
        position.x += velocity.x
        position.y += velocity.y
    }

    fun isLookingAt(t: ImmutableIntVector2): Boolean = isLookingAt(t.x, t.y)

    fun isLookingAt(tx: Int, ty: Int): Boolean =
        isLookingAtX(tx) || isLookingAtY(ty)

    fun isLookingAtX(tx: Int): Boolean =
        (tx - position.x).sign == velocity.x

    fun isLookingAtY(ty: Int): Boolean =
        (ty - position.y).sign == velocity.y
}