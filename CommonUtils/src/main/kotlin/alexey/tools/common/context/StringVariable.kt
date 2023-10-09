package alexey.tools.common.context

class StringVariable(var value: String? = ""): BasicVariable() {
    override fun toBoolean(): Boolean = value?.toBoolean() ?: false
    override fun toByte(): Byte = value?.toByteOrNull() ?: 0
    override fun toDouble(): Double = value?.toDoubleOrNull() ?: 0.0
    override fun toFloat(): Float = value?.toFloatOrNull() ?: 0F
    override fun toInt(): Int = value?.toIntOrNull() ?: 0
    override fun toShort(): Short = value?.toShortOrNull() ?: 0
    override fun toLong(): Long = value?.toLongOrNull() ?: 0L
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