package alexey.tools.common.context

class ShortVariable(var value: Short = 0): BasicVariable() {
    override fun toBoolean(): Boolean = value != ZERO
    override fun toByte(): Byte = value.toByte()
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value
    override fun toLong(): Long = value.toLong()
    override fun toString(): String = value.toString()
    override fun getValue(): Any = value
    override fun type(): Byte = ImmutableVariable.INTEGER
    override fun isInvalid(): Boolean = value == Short.MIN_VALUE



    override fun invalidate() {
        set(Short.MIN_VALUE)
    }

    override fun set(number: Short) {
        value = number
        notifyChange()
    }

    override fun set(number: Number) {
        set(number.toShort())
    }

    override fun set(number: Byte) {
        set(number.toShort())
    }

    override fun set(number: Int) {
        set(number.toShort())
    }

    override fun set(number: Long) {
        set(number.toShort())
    }

    override fun set(number: Float) {
        set(number.toInt())
    }

    override fun set(number: Double) {
        set(number.toInt())
    }

    override fun set(boolean: Boolean) {
        set(if (boolean) ONE else ZERO)
    }

    override fun set(string: String) {
        set(string.toShortOrNull() ?: ZERO)
    }

    override fun copy(): Variable = ShortVariable(value)


    companion object {
        const val ZERO: Short = 0
        const val ONE: Short = 1
    }
}