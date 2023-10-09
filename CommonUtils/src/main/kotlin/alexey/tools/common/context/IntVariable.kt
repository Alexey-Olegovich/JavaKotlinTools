package alexey.tools.common.context

class IntVariable(var value: Int = 0): BasicVariable() {
    override fun toBoolean(): Boolean = value != 0
    override fun toByte(): Byte = value.toByte()
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toInt(): Int = value
    override fun toShort(): Short = value.toShort()
    override fun toLong(): Long = value.toLong()
    override fun toString(): String = value.toString()
    override fun getValue(): Any = value
    override fun type(): Byte = ImmutableVariable.INTEGER
    override fun isInvalid(): Boolean = value == Int.MIN_VALUE



    override fun invalidate() {
        set(Int.MIN_VALUE)
    }

    override fun set(number: Int) {
        value = number
        notifyChange()
    }

    override fun set(number: Number) {
        set(number.toInt())
    }

    override fun set(number: Byte) {
        set(number.toInt())
    }

    override fun set(number: Short) {
        set(number.toInt())
    }

    override fun set(number: Long) {
        set(number.toInt())
    }

    override fun set(number: Float) {
        set(number.toInt())
    }

    override fun set(number: Double) {
        set(number.toInt())
    }

    override fun set(boolean: Boolean) {
        set(if (boolean) 1 else 0)
    }

    override fun set(string: String) {
        set(string.toIntOrNull() ?: 0)
    }

    override fun copy(): Variable = IntVariable(value)
}