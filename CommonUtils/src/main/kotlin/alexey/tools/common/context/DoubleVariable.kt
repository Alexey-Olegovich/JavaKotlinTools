package alexey.tools.common.context

class DoubleVariable(var value: Double = 0.0): BasicVariable() {
    override fun toBoolean(): Boolean = value != 0.0
    override fun toByte(): Byte = value.toInt().toByte()
    override fun toDouble(): Double = value
    override fun toFloat(): Float = value.toFloat()
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value.toInt().toShort()
    override fun toLong(): Long = value.toLong()
    override fun toString(): String = value.toString()
    override fun getValue(): Any = value
    override fun type(): Byte = ImmutableVariable.DECIMAL
    override fun isInvalid(): Boolean = value.isNaN()



    override fun invalidate() {
        set(Double.NaN)
    }

    override fun set(number: Number) {
        value = number.toDouble()
        notifyChange()
    }

    override fun set(number: Byte) {
        value = number.toDouble()
        notifyChange()
    }

    override fun set(number: Short) {
        value = number.toDouble()
        notifyChange()
    }

    override fun set(number: Long) {
        value = number.toDouble()
        notifyChange()
    }

    override fun set(number: Int) {
        value = number.toDouble()
        notifyChange()
    }

    override fun set(number: Float) {
        value = number.toDouble()
        notifyChange()
    }

    override fun set(number: Double) {
        value = number
        notifyChange()
    }

    override fun set(boolean: Boolean) {
        value = if (boolean) 1.0 else 0.0
        notifyChange()
    }

    override fun set(string: String) {
        value = string.toDoubleOrNull() ?: 0.0
        notifyChange()
    }

    override fun copy(): Variable = DoubleVariable(value)
}