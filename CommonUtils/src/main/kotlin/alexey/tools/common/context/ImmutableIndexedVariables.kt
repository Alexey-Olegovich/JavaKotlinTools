package alexey.tools.common.context

interface ImmutableIndexedVariables {

    fun asList(): List<Variable?> = emptyList()



    fun getVariable(name: Int): ImmutableVariable? = null



    fun getString(name: Int, defaultValue: String = ""): String =
        getVariable(name)?.toString() ?: defaultValue

    fun getBoolean(name: Int, defaultValue: Boolean = false): Boolean =
        getVariable(name)?.toBoolean() ?: defaultValue

    fun getInt(name: Int, defaultValue: Int = 0): Int =
        getVariable(name)?.toInt() ?: defaultValue

    fun getDouble(name: Int, defaultValue: Double = 0.0): Double =
        getVariable(name)?.toDouble() ?: defaultValue

    fun getFloat(name: Int, defaultValue: Float = 0F): Float =
        getVariable(name)?.toFloat() ?: defaultValue

    fun getShort(name: Int, defaultValue: Short = 0): Short =
        getVariable(name)?.toShort() ?: defaultValue

    fun getByte(name: Int, defaultValue: Byte = 0): Byte =
        getVariable(name)?.toByte() ?: defaultValue

    fun getLong(name: Int, defaultValue: Long = 0): Long =
        getVariable(name)?.toLong() ?: defaultValue



    fun get(pair: Pair<Int, String>, defaultValue: String = pair.second): String = getString(pair.first, defaultValue)

    fun get(pair: Pair<Int, Float>, defaultValue: Float = pair.second): Float = getFloat(pair.first, defaultValue)

    fun get(pair: Pair<Int, Boolean>, defaultValue: Boolean = pair.second): Boolean = getBoolean(pair.first, defaultValue)

    fun get(pair: Pair<Int, Byte>, defaultValue: Byte = pair.second): Byte = getByte(pair.first, defaultValue)

    fun get(pair: Pair<Int, Short>, defaultValue: Short = pair.second): Short = getShort(pair.first, defaultValue)

    fun get(pair: Pair<Int, Double>, defaultValue: Double = pair.second): Double = getDouble(pair.first, defaultValue)

    fun get(pair: Pair<Int, Int>, defaultValue: Int = pair.second): Int = getInt(pair.first, defaultValue)

    fun get(pair: Pair<Int, Long>, defaultValue: Long = pair.second): Long = getLong(pair.first, defaultValue)



    fun size() = asList().size

    fun isEmpty() = asList().isEmpty()



    operator fun plus(variables: ImmutableIndexedVariables): ImmutableIndexedVariables {
        if (isEmpty()) return variables
        if (variables.isEmpty()) return this
        return IndexedVariables(this).apply { put(variables) }
    }



    companion object {
        val DEFAULT = object : ImmutableIndexedVariables {}
    }
}