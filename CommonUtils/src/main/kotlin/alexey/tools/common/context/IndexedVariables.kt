package alexey.tools.common.context

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.getOrExtendSet

class IndexedVariables(copy: ImmutableIndexedVariables = ImmutableIndexedVariables.DEFAULT): ImmutableIndexedVariables {

    private val variables = ObjectList<Variable>(8)



    init { put(copy) }



    fun obtain(name: Int, defaultValue: Float = 0F): Float =
        obtainVariable(name, defaultValue).toFloat()

    fun obtain(name: Int, defaultValue: Double = 0.0): Double =
        obtainVariable(name, defaultValue).toDouble()

    fun obtain(name: Int, defaultValue: Byte = 0): Byte =
        obtainVariable(name, defaultValue).toByte()

    fun obtain(name: Int, defaultValue: Int = 0): Int =
        obtainVariable(name, defaultValue).toInt()

    fun obtain(name: Int, defaultValue: Long = 0L): Long =
        obtainVariable(name, defaultValue).toLong()

    fun obtain(name: Int, defaultValue: Short = 0): Short =
        obtainVariable(name, defaultValue).toShort()

    fun obtain(name: Int, defaultValue: Boolean = false): Boolean =
        obtainVariable(name, defaultValue).toBoolean()

    fun obtain(name: Int, defaultValue: String = ""): String =
        obtainVariable(name, defaultValue).toString()



    fun obtain(pair: Pair<Int, Float>): Float = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<Int, Double>): Double = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<Int, Byte>): Byte = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<Int, Int>): Int = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<Int, Long>): Long = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<Int, Short>): Short = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<Int, Boolean>): Boolean = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<Int, String>): String = obtain(pair.first, pair.second)



    fun obtainVariable(name: Int, defaultValue: String = ""): Variable =
        variables.getOrExtendSet(name) { StringVariable(defaultValue) }

    fun obtainVariable(name: Int, defaultValue: Float = 0F): Variable =
        variables.getOrExtendSet(name) { FloatVariable(defaultValue) }

    fun obtainVariable(name: Int, defaultValue: Double = 0.0): Variable =
        variables.getOrExtendSet(name) { DoubleVariable(defaultValue) }

    fun obtainVariable(name: Int, defaultValue: Int = 0): Variable =
        variables.getOrExtendSet(name) { IntVariable(defaultValue) }

    fun obtainVariable(name: Int, defaultValue: Long = 0L): Variable =
        variables.getOrExtendSet(name) { LongVariable(defaultValue) }

    fun obtainVariable(name: Int, defaultValue: Short = 0): Variable =
        variables.getOrExtendSet(name) { ShortVariable(defaultValue) }

    fun obtainVariable(name: Int, defaultValue: Byte = 0): Variable =
        variables.getOrExtendSet(name) { ByteVariable(defaultValue) }

    fun obtainVariable(name: Int, defaultValue: Boolean = false): Variable =
        variables.getOrExtendSet(name) { BooleanVariable(defaultValue) }



    fun obtainVariable(pair: Pair<Int, Any>): Variable =
        variables.getOrExtendSet(pair.first) { Variables.obtain(pair.second) }



    fun put(other: ImmutableIndexedVariables) {
        val variableList = other.asList()
        variables.ensureSize(variableList.size)
        for (i in variableList.indices)
            variables.justSet(i, (variableList[i] ?: continue).copy())
    }

    fun put(name: Int, value: Double) {
        variables[name] = DoubleVariable(value)
    }

    fun put(name: Int, value: Float) {
        variables[name] = FloatVariable(value)
    }

    fun put(name: Int, value: Int) {
        variables[name] = IntVariable(value)
    }

    fun put(name: Int, value: Long) {
        variables[name] = LongVariable(value)
    }

    fun put(name: Int, value: Short) {
        variables[name] = ShortVariable(value)
    }

    fun put(name: Int, value: Byte) {
        variables[name] = ByteVariable(value)
    }

    fun put(name: Int, value: Boolean) {
        variables[name] = BooleanVariable(value)
    }

    fun put(name: Int, value: String) {
        variables[name] = StringVariable(value)
    }



    fun set(other: ImmutableIndexedVariables) {
        val variableList = other.asList()
        variables.ensureSize(variableList.size)
        for (i in variableList.indices) {
            val variable = variableList[i] ?: continue
            val localVariable = variables[i]
            if (localVariable == null)
                variables.justSet(i, variable.copy()) else
                localVariable.set(variable.value)
        }
    }

    fun set(name: Int, value: Double) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, DoubleVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: Float) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, FloatVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: Int) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, IntVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: Long) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, LongVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: Short) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, ShortVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: Byte) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, ByteVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: Boolean) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, BooleanVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: String) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, StringVariable(value)) else
            variable.set(value)
    }

    fun set(name: Int, value: Any) {
        variables.ensureSpace(name)
        val variable = variables[name]
        if (variable == null)
            variables.justSet(name, Variables.obtain(value)) else
            variable.set(value)
    }



    fun set(pair: Pair<Int, String>, value: String = pair.second) = set(pair.first, value)

    fun set(pair: Pair<Int, Float>, value: Float = pair.second) = set(pair.first, value)

    fun set(pair: Pair<Int, Boolean>, value: Boolean = pair.second) = set(pair.first, value)

    fun set(pair: Pair<Int, Byte>, value: Byte = pair.second) = set(pair.first, value)

    fun set(pair: Pair<Int, Short>, value: Short = pair.second) = set(pair.first, value)

    fun set(pair: Pair<Int, Double>, value: Double = pair.second) = set(pair.first, value)

    fun set(pair: Pair<Int, Int>, value: Int = pair.second) = set(pair.first, value)

    fun set(pair: Pair<Int, Long>, value: Long = pair.second) = set(pair.first, value)



    fun remove(name: Int): ImmutableVariable? = variables.setNull(name)

    fun clear() {
        while (variables.isNotEmpty) variables.removeLast()?.invalidate()
    }



    override fun asList(): List<Variable?> = variables

    override fun getVariable(name: Int): Variable? = variables[name]
}