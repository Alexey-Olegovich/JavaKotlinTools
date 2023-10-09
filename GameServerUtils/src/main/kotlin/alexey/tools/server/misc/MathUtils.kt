package alexey.tools.server.misc

import alexey.tools.common.converters.x
import alexey.tools.common.converters.y
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.math.*

fun distanceToLine(point: Vector2, linePointA: Vector2, linePointB: Vector2): Float {
    val a = linePointA.y - linePointB.y
    val b = linePointB.x - linePointA.x
    val c = linePointB.y * linePointA.x - linePointA.y * linePointB.x
    return abs(a * point.x + b * point.y + c) / sqrt(a * a + b * b)
}

fun nearestPointOnLine(point: Vector2, linePointA: Vector2, linePointB: Vector2): Vector2 {
    val a = linePointA.y - linePointB.y
    val b = linePointB.x - linePointA.x
    val c = linePointB.y * linePointA.x - linePointA.y * linePointB.x
    val normalDistance = abs(a * point.x + b * point.y + c) / sqrt(a * a + b * b)
    val lineNormal = Vector2(-b, a).nor()
    val length = sqrt(point.dst2(linePointB) - normalDistance * normalDistance)
    return Vector2(linePointB.x + lineNormal.x * length, linePointB.y + lineNormal.y * length)
}

fun angle(point: Vector2, linePointA: Vector2, linePointB: Vector2): Float {
    val lineV = Vector2(linePointA.x - linePointB.x, linePointA.y - linePointB.y)
    val pointV = Vector2(point.x - linePointB.x, point.y - linePointB.y)
    val angle = (pointV.x * lineV.x + pointV.y * lineV.y) / (point.dst(linePointB) * linePointA.dst(linePointB))
    return acos(angle) * MathUtils.radiansToDegrees
}

fun pointInRectangle(point: Vector2, boxPosition: Vector2, boxSize: Vector2): Boolean =
    point.x >= boxPosition.x && point.y >= boxPosition.y &&
            point.x <= boxPosition.x + boxSize.x && point.y <= boxPosition.y + boxSize.y

fun nearestPoint(mainPoint: Vector2, otherPoints: Array<Vector2>): Vector2 =
    nearestPoint(mainPoint, otherPoints.asList())

fun nearestPoint(mainPoint: Vector2, otherPoints: List<Vector2>): Vector2 =
    if(otherPoints.isNotEmpty()) {
        var tempLength: Float
        var nearestPoint = otherPoints.first()
        var length = mainPoint.dst(nearestPoint)
        for (i in 1 ..< otherPoints.size) {
            val point = otherPoints[i]
            tempLength = point.dst(mainPoint)
            if (tempLength < length) {
                nearestPoint = point
                length = tempLength
            }
        }
        nearestPoint
    } else
        mainPoint

fun nearestPointOnEdge(mainPoint: Vector2, polygon: Array<Vector2>): Vector2 =
    nearestPointOnEdge(mainPoint, polygon.asList())

fun nearestPointOnEdge(mainPoint: Vector2, polygon: List<Vector2>): Vector2 =
    if(polygon.size > 1) {
        val first = polygon[0]
        var tempLength: Float
        var nearestPoint = first
        var length = mainPoint.dst(nearestPoint)
        for (point in 1 ..< polygon.size) {
            var ang1 = angle(mainPoint, polygon[point - 1], polygon[point])
            var ang2 = angle(mainPoint, polygon[point], polygon[point - 1])
            if(ang1 <= 90 && ang2 <= 90){
                tempLength = distanceToLine(mainPoint, polygon[point - 1], polygon[point])
                if(tempLength < length){
                    length = tempLength
                    nearestPoint = nearestPointOnLine(mainPoint, polygon[point - 1], polygon[point])
                }
            } else {
                if( ang1 > 90 && ang2 <= 90) {
                    tempLength = polygon[point].dst(mainPoint)
                    if(tempLength < length) {
                        nearestPoint = polygon[point]
                        length = tempLength
                    }
                } else {
                    tempLength = polygon[point-1].dst(mainPoint)
                    if(tempLength < length) {
                        nearestPoint = polygon[point - 1]
                        length = tempLength
                    }
                }
            }
            if( point == polygon.size - 1 ){
                ang1 = angle(mainPoint, polygon[point], first)
                ang2 = angle(mainPoint, first, polygon[point])
                if(ang1 <= 90 && ang2 <= 90){
                    tempLength = distanceToLine(mainPoint, polygon[point], first)
                    if(tempLength < length){
                        length = tempLength
                        nearestPoint = nearestPointOnLine(mainPoint, polygon[point], first)
                    }
                } else {
                    if( ang1 > 90 && ang2 <= 90) {
                        tempLength = first.dst(mainPoint)
                        if(tempLength < length) {
                            nearestPoint = first
                            length = tempLength
                        }
                    } else {
                        tempLength = polygon[point].dst(mainPoint)
                        if(tempLength < length) {
                            nearestPoint = polygon[point]
                            length = tempLength
                        }
                    }
                }
            }
        }
        nearestPoint
    } else if (polygon.size == 1) polygon[0] else mainPoint

fun toGrid(pointX: Float, pointY: Float, xDelta: Float, yDelta: Float): Vector2 =
    Vector2(
        round(pointX / xDelta) * xDelta,
        round(pointY / yDelta) * yDelta)

fun toGrid(pointX: Float, pointY: Float, delta: Vector2): Vector2 =
    toGrid(pointX, pointY, delta.x, delta.y)

fun toGrid(point: Vector2, delta: Vector2): Vector2 =
    toGrid(point.x, point.y, delta.x, delta.y)

fun toVector2(vector3: Vector3): Vector2 = Vector2(vector3.x, vector3.y)

fun toVector3(vector2: Vector2): Vector3 = Vector3(vector2.x, vector2.y, 0f)

fun calcNormal(p1: Vector3, p2: Vector3, p3: Vector3): Vector3 {
    val v1 = p2.sub(p1)
    val v2 = p2.sub(p3)
    return Vector3(
        v1.y * v2.z - v1.z * v2.y,
        v1.z * v2.x - v1.x * v2.z,
        v1.x * v2.y - v1.y * v2.x)
}

fun direction(angle: Float): Vector2 = Vector2(cos(angle), sin(angle))

fun Vector2.rotateAroundRad(cx: Float, cy: Float, angle: Float) {
    val s = sin(angle)
    val c = cos(angle)
    x -= cx
    y -= cy
    val nx = x * c - y * s
    val ny = x * s + y * c
    x = nx + cx
    y = ny + cy
}

fun Vector3.add(vector2: Vector2): Vector3 {
    x += vector2.x
    y += vector2.y
    return this
}

fun Vector3.add(x: Float, y: Float): Vector3 {
    this.x += x
    this.y += y
    return this
}

fun Vector2.set(v: Long) { set(v.x, v.y) }

fun Vector2.toLong(): Long = alexey.tools.common.converters.toLong(x, y)

fun Vector2.nor4(): Vector2 {
    when {
        x == 0F -> {
            if (y == 0F) return this
            y = if (y < 0F) -1F else 1F
        }
        y == 0F -> {
            x = if (x < 0F) -1F else 1F
        }
        abs(x) > abs(y) -> {
            x = if (x < 0F) -1F else 1F
            y = 0F
        }
        else -> {
            y = if (y < 0F) -1F else 1F
            x = 0F
        }
    }
    return this
}

fun Vector2.flip(): Vector2 {
    x = -x
    y = -y
    return this
}