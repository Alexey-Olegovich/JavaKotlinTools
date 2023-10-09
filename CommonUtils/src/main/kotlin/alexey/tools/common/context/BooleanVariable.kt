package alexey.tools.common.context

open class BooleanVariable(var value: Byte = FALSE): BasicVariable() {
    constructor(value: Boolean): this(if (value) TRUE else FALSE)

    override fun toDouble(): Double = if (toBoolean()) 1.0 else 0.0
    override fun toFloat(): Float = if (toBoolean()) 1.0F else 0.0F
    override fun toByte(): Byte = if (toBoolean()) TRUE else FALSE
    override fun toInt(): Int = if (toBoolean()) 1 else 0
    override fun toShort(): Short = if (toBoolean()) 1 else 0
    override fun toLong(): Long = if (toBoolean()) 1L else 0L
    override fun toBoolean(): Boolean = value != FALSE
    override fun toString(): String = if (toBoolean()) "true" else "false"
    override fun getValue(): Any = value
    override fun type(): Byte = ImmutableVariable.BOOLEAN
    override fun isInvalid(): Boolean = value == Byte.MIN_VALUE



    override fun invalidate() {
        set(Byte.MIN_VALUE)
    }

    override fun set(number: Byte) {
        value = number
        notifyChange()
    }

    override fun set(number: Number) {
        set(number.toByte())
    }

    override fun set(number: Short) {
        set(number.toByte())
    }

    override fun set(number: Long) {
        set(number.toByte())
    }

    override fun set(number: Int) {
        set(number.toByte())
    }

    override fun set(number: Float) {
        set(number.toInt().toByte())
    }

    override fun set(number: Double) {
        set(number.toInt().toByte())
    }

    override fun set(boolean: Boolean) {
        set(if (boolean) TRUE else FALSE)
    }

    override fun set(string: String) {
        set(if (string == "true") TRUE else FALSE)
    }

    override fun copy(): Variable = BooleanVariable(value)



    companion object {
        const val FALSE: Byte = 0
        const val TRUE: Byte = 1
    }
}