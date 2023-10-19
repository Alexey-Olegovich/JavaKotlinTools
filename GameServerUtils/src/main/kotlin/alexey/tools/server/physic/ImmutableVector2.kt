package alexey.tools.server.physic

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

class ImmutableVector2(val x: Float, val y: Float): Serializable, Vector<Vector2> {

    override fun dot(v: Vector2): Float {
        return x * v.x + y * v.y
    }

    override fun cpy(): Vector2 {
        return Vector2(x, y)
    }

    override fun len(): Float {
        return sqrt(x * x + y * y)
    }

    override fun len2(): Float {
        return x * x + y * y
    }

    override fun isUnit(): Boolean {
        return isUnit(0.000000001f)
    }

    override fun isUnit(margin: Float): Boolean {
        return abs(len2() - 1f) < margin
    }

    override fun isZero(): Boolean {
        return x == 0F && y == 0F
    }

    override fun isZero(margin: Float): Boolean {
        return len2() < margin
    }

    override fun epsilonEquals(other: Vector2?, epsilon: Float): Boolean {
        if (other == null) return false
        if (abs(other.x - x) > epsilon) return false
        return abs(other.y - y) <= epsilon
    }

    override fun hasOppositeDirection(other: Vector2): Boolean {
        return dot(other) < 0
    }

    override fun hasSameDirection(other: Vector2): Boolean {
        return dot(other) > 0
    }

    override fun isPerpendicular(other: Vector2, epsilon: Float): Boolean {
        return MathUtils.isZero(dot(other), epsilon)
    }

    override fun isPerpendicular(other: Vector2): Boolean {
        return MathUtils.isZero(dot(other))
    }

    override fun isCollinearOpposite(other: Vector2): Boolean {
        return isOnLine(other) && dot(other) < 0f
    }

    override fun isCollinearOpposite(other: Vector2, epsilon: Float): Boolean {
        return isOnLine(other, epsilon) && dot(other) < 0f
    }

    override fun isCollinear(other: Vector2): Boolean {
        return isOnLine(other) && dot(other) > 0f
    }

    override fun isCollinear(other: Vector2, epsilon: Float): Boolean {
        return isOnLine(other, epsilon) && dot(other) > 0f
    }

    override fun isOnLine(other: Vector2): Boolean {
        return MathUtils.isZero(x * other.y - y * other.x)
    }

    override fun isOnLine(other: Vector2, epsilon: Float): Boolean {
        return MathUtils.isZero(x * other.y - y * other.x, epsilon)
    }

    override fun dst2(v: Vector2): Float {
        val xd = v.x - x
        val yd = v.y - y
        return xd * xd + yd * yd
    }

    override fun dst(v: Vector2): Float {
        val xd = v.x - x
        val yd = v.y - y
        return sqrt(xd * xd + yd * yd)
    }

    override fun set(v: Vector2?) = throw UnsupportedOperationException()
    override fun setLength(len: Float) = throw UnsupportedOperationException()
    override fun setLength2(len2: Float) = throw UnsupportedOperationException()
    override fun setToRandomDirection() = throw UnsupportedOperationException()
    override fun setZero() = throw UnsupportedOperationException()
    override fun add(v: Vector2?) = throw UnsupportedOperationException()
    override fun mulAdd(vec: Vector2?, mulVec: Vector2?) = throw UnsupportedOperationException()
    override fun mulAdd(vec: Vector2?, scalar: Float) = throw UnsupportedOperationException()
    override fun sub(v: Vector2?) = throw UnsupportedOperationException()
    override fun scl(scalar: Float) = throw UnsupportedOperationException()
    override fun scl(v: Vector2?) = throw UnsupportedOperationException()
    override fun nor() = throw UnsupportedOperationException()
    override fun limit(limit: Float) = throw UnsupportedOperationException()
    override fun limit2(limit2: Float) = throw UnsupportedOperationException()
    override fun clamp(min: Float, max: Float) = throw UnsupportedOperationException()
    override fun lerp(target: Vector2?, alpha: Float) = throw UnsupportedOperationException()
    override fun interpolate(target: Vector2?, alpha: Float, interpolation: Interpolation?) = throw UnsupportedOperationException()


}