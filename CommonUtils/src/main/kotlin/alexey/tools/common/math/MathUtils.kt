package alexey.tools.common.math

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*

fun gcd(a: Int, b: Int): Int {
    if (b == 0) return abs(a)
    return gcd(b, a % b)
}

fun Random.roll(chance: Int = 50): Boolean = nextInt(100) < chance

fun Random.preciseRoll(chance: Double): Boolean = nextDouble() < chance

fun mod(a: Float, b: Float): Float = a - floor(a / b) * b

fun roll(winChance: Double): Boolean = ThreadLocalRandom.current().nextDouble() < winChance

fun randomInt(): Int = ThreadLocalRandom.current().nextInt()

fun randomLong(): Long = ThreadLocalRandom.current().nextLong()

fun randomBoolean(): Boolean = ThreadLocalRandom.current().nextBoolean()

fun randomFloat(): Float = ThreadLocalRandom.current().nextFloat()

fun randomFloat(origin: Float, bound: Float): Float = randomFloat() * (bound - origin) + origin

fun randomLong(bound: Long): Long = ThreadLocalRandom.current().nextLong(bound)

fun randomLong(origin: Long, bound: Long): Long = ThreadLocalRandom.current().nextLong(origin, bound)

fun randomInt(bound: Int): Int = ThreadLocalRandom.current().nextInt(bound)

fun randomInt(origin: Int, bound: Int): Int = ThreadLocalRandom.current().nextInt(origin, bound)

fun randomDouble(origin: Double, bound: Double): Double = ThreadLocalRandom.current().nextDouble(origin, bound)

fun exponentialRandomDouble(m: Double): Double = ln(1 - ThreadLocalRandom.current().nextDouble()) / (-1.0 / m)

fun randomDouble(): Double = ThreadLocalRandom.current().nextDouble()

fun toGrid(x: Float, y: Float, dx: Int, dy: Int) =
    IntVector2((x / dx).roundToInt() * dx, (y / dy).roundToInt() * dy)

fun toGrid(x: Float, y: Float, dx: Float, dy: Float = dx) =
    IntVector2(toGrid(x, dx), toGrid(y, dy))

fun toGrid(x: Float, dx: Float): Int = floor((x / dx).toDouble()).toInt()

inline fun forSquare(x: Int, y: Int, radius: Int, action: (Int, Int) -> Unit) {
    for (i in x - radius .. x + radius) for (j in y - radius .. y + radius) action(i, j)
}

inline fun forSquare(bx: Int, by: Int, ex: Int, ey: Int, action: (Int, Int) -> Unit) {
    for (i in bx .. ex) for (j in by .. ey) action(i, j)
}

inline fun ImmutableIntMatrix2.forEach(action: (Int, Int, Int) -> Unit) {
    for (x in 0 ..< width) for (y in 0 ..< height) action(x, y, get(x, y))
}

inline fun <T> Collection<T>.avgOf(selector: (T) -> Int): Int {
    var sum = 0
    forEach { sum += selector(it) }
    return sum / size
}

inline fun <T> Array<T>.avgOf(selector: (T) -> Int): Int {
    var sum = 0
    forEach { sum += selector(it) }
    return sum / size
}

const val DOUBLE_PI = PI.toFloat() * 2F