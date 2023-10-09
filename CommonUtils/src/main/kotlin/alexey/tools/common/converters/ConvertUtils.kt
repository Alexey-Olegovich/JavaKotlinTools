package alexey.tools.common.converters

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.Inflater

fun ByteArray.decompressZLib(): ByteArray {
    val inflater = Inflater()
    inflater.setInput(this)
    val outputStream = ByteArrayOutputStream(size)
    val buffer = ByteArray(1024)
    while (!inflater.finished()) {
        val count = inflater.inflate(buffer)
        outputStream.write(buffer, 0, count)
    }
    outputStream.close()
    inflater.end()
    return outputStream.toByteArray()
}

fun ByteArray.toIntArray(): IntArray = ByteBuffer.wrap(this, 0, size)
    .order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().run { IntArray(remaining()).also { get(it) } }

fun toLong(x: Float, y: Float) = x.toRawBits().toLong().shl(32).or(y.toRawBits().toUInt().toLong())

val Long.x: Float get() = Float.fromBits(first)

val Long.y: Float get() = Float.fromBits(second)

val Long.first: Int get() = ushr(32).toInt()

val Long.second: Int get() = and(LONG_BEGIN).toInt()

const val LONG_BEGIN = 0x0000_0000_FFFF_FFFFL