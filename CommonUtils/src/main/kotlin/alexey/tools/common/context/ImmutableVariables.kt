package alexey.tools.common.context

import alexey.tools.common.misc.Injector

interface ImmutableVariables: Injector {

    fun asMap(): Map<String, ImmutableVariable> = emptyMap()



    fun getVariable(name: String): ImmutableVariable? = null



    fun getString(name: String, defaultValue: String = ""): String =
        getVariable(name)?.toString() ?: defaultValue

    fun getBoolean(name: String, defaultValue: Boolean = false): Boolean =
        getVariable(name)?.toBoolean() ?: defaultValue

    fun getInt(name: String, defaultValue: Int = 0): Int =
        getVariable(name)?.toInt() ?: defaultValue

    fun getDouble(name: String, defaultValue: Double = 0.0): Double =
        getVariable(name)?.toDouble() ?: defaultValue

    fun getFloat(name: String, defaultValue: Float = 0F): Float =
        getVariable(name)?.toFloat() ?: defaultValue

    fun getShort(name: String, defaultValue: Short = 0): Short =
        getVariable(name)?.toShort() ?: defaultValue

    fun getByte(name: String, defaultValue: Byte = 0): Byte =
        getVariable(name)?.toByte() ?: defaultValue

    fun getLong(name: String, defaultValue: Long = 0): Long =
        getVariable(name)?.toLong() ?: defaultValue



    fun get(pair: Pair<String, String>, defaultValue: String = pair.second): String = getString(pair.first, defaultValue)

    fun get(pair: Pair<String, Float>, defaultValue: Float = pair.second): Float = getFloat(pair.first, defaultValue)

    fun get(pair: Pair<String, Boolean>, defaultValue: Boolean = pair.second): Boolean = getBoolean(pair.first, defaultValue)

    fun get(pair: Pair<String, Byte>, defaultValue: Byte = pair.second): Byte = getByte(pair.first, defaultValue)

    fun get(pair: Pair<String, Short>, defaultValue: Short = pair.second): Short = getShort(pair.first, defaultValue)

    fun get(pair: Pair<String, Double>, defaultValue: Double = pair.second): Double = getDouble(pair.first, defaultValue)

    fun get(pair: Pair<String, Int>, defaultValue: Int = pair.second): Int = getInt(pair.first, defaultValue)

    fun get(pair: Pair<String, Long>, defaultValue: Long = pair.second): Long = getLong(pair.first, defaultValue)



    fun size() = asMap().size

    fun isEmpty() = asMap().isEmpty()



    operator fun plus(variables: ImmutableVariables): ImmutableVariables {
        if (isEmpty()) return variables
        if (variables.isEmpty()) return this
        return Variables(this).apply { put(variables) }
    }



    companion object {
        val DEFAULT = object : ImmutableVariables {}
    }
}