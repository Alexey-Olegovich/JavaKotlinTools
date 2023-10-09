package alexey.tools.common.context

class ByteVariable(value: Byte = 0): BooleanVariable(value) {
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toByte(): Byte = value
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value.toShort()
    override fun toLong(): Long = value.toLong()
    override fun toString(): String = value.toString()
    override fun type(): Byte = ImmutableVariable.INTEGER



    override fun set(string: String) {
        set(string.toByteOrNull() ?: FALSE)
    }

    override fun copy(): Variable = ByteVariable(value)
}