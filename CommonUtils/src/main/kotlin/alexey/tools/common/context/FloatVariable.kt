package alexey.tools.common.context

class FloatVariable(var value: Float = 0F): BasicVariable() {
    override fun toBoolean(): Boolean = value != 0F
    override fun toByte(): Byte = value.toInt().toByte()
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value.toInt().toShort()
    override fun toLong(): Long = value.toLong()
    override fun toString(): String = value.toString()
    override fun getValue(): Any = value
    override fun type(): Byte = ImmutableVariable.DECIMAL



    override fun set(number: Number) {
        value = number.toFloat()
        notifyChange()
    }

    override fun set(number: Byte) {
        value = number.toFloat()
        notifyChange()
    }

    override fun set(number: Short) {
        value = number.toFloat()
        notifyChange()
    }

    override fun set(number: Long) {
        value = number.toFloat()
        notifyChange()
    }

    override fun set(number: Int) {
        value = number.toFloat()
        notifyChange()
    }

    override fun set(number: Float) {
        value = number
        notifyChange()
    }

    override fun set(number: Double) {
        value = number.toFloat()
        notifyChange()
    }

    override fun set(boolean: Boolean) {
        value = if (boolean) 1F else 0F
        notifyChange()
    }

    override fun set(string: String) {
        value = string.toFloatOrNull() ?: 0F
        notifyChange()
    }

    override fun copy(): Variable = FloatVariable(value)
}