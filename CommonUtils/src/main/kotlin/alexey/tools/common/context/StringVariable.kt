package alexey.tools.common.context

class StringVariable(var value: String? = ""): BasicVariable() {
    override fun toBoolean(): Boolean = value?.toBoolean() ?: false
    override fun toByte() = value?.run { toShortOrNull(10) ?: toShortOrNull(2) ?: toShortOrNull(16) ?: 0 }?.toByte() ?: 0
    override fun toDouble(): Double = value?.toDoubleOrNull() ?: 0.0
    override fun toFloat(): Float = value?.toFloatOrNull() ?: 0F
    override fun toInt() = toLong().toInt()
    override fun toShort() = value?.run { toIntOrNull(10) ?: toIntOrNull(2) ?: toIntOrNull(16) ?: 0 }?.toShort() ?: 0
    override fun toLong() = value?.run { toLongOrNull(10) ?: toLongOrNull(2) ?: toLongOrNull(16) ?: 0L } ?: 0L
    override fun toString(): String = value ?: ""
    override fun getValue(): Any = value ?: ""
    override fun type(): Byte = ImmutableVariable.STRING
    override fun isValid(): Boolean = value != null
    override fun isInvalid(): Boolean = value == null



    override fun invalidate() {
        value = null
        notifyChange()
    }

    override fun set(number: Number) {
        value = number.toString()
        notifyChange()
    }

    override fun set(boolean: Boolean) {
        value = if (boolean) "true" else "false"
        notifyChange()
    }

    override fun set(string: String) {
        value = string
        notifyChange()
    }

    override fun copy(): Variable = StringVariable(value)
}