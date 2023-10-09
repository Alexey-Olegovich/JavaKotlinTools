package alexey.tools.common.context

class LongVariable(var value: Long = 0L): BasicVariable() {
    override fun toBoolean(): Boolean = value != 0L
    override fun toByte(): Byte = value.toByte()
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value.toShort()
    override fun toLong(): Long = value
    override fun toString(): String = value.toString()
    override fun getValue(): Any = value
    override fun type(): Byte = ImmutableVariable.INTEGER
    override fun isInvalid(): Boolean = value == Long.MIN_VALUE



    override fun invalidate() {
        set(Long.MIN_VALUE)
    }

    override fun set(number: Long) {
        value = number
        notifyChange()
    }

    override fun set(number: Number) {
        set(number.toLong())
    }

    override fun set(number: Byte) {
        set(number.toLong())
    }

    override fun set(number: Short) {
        set(number.toLong())
    }

    override fun set(number: Int) {
        set(number.toLong())
    }

    override fun set(number: Float) {
        set(number.toLong())
    }

    override fun set(number: Double) {
        set(number.toLong())
    }

    override fun set(boolean: Boolean) {
        set(if (boolean) 1L else 0L)
    }

    override fun set(string: String) {
        set(string.toLongOrNull() ?: 0L)
    }

    override fun copy(): Variable = LongVariable(value)
}