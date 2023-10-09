package alexey.tools.common.context

import java.lang.reflect.Constructor
import java.util.IdentityHashMap

class Variables(copy: ImmutableVariables = ImmutableVariables.DEFAULT): ImmutableVariables {

    private val variables = HashMap<String, Variable>()



    init { put(copy) }



    fun obtain(name: String, defaultValue: Float = 0F): Float =
        obtainVariable(name, defaultValue).toFloat()

    fun obtain(name: String, defaultValue: Double = 0.0): Double =
        obtainVariable(name, defaultValue).toDouble()

    fun obtain(name: String, defaultValue: Byte = 0): Byte =
        obtainVariable(name, defaultValue).toByte()

    fun obtain(name: String, defaultValue: Int = 0): Int =
        obtainVariable(name, defaultValue).toInt()

    fun obtain(name: String, defaultValue: Long = 0L): Long =
        obtainVariable(name, defaultValue).toLong()

    fun obtain(name: String, defaultValue: Short = 0): Short =
        obtainVariable(name, defaultValue).toShort()

    fun obtain(name: String, defaultValue: Boolean = false): Boolean =
        obtainVariable(name, defaultValue).toBoolean()

    fun obtain(name: String, defaultValue: String = ""): String =
        obtainVariable(name, defaultValue).toString()



    fun obtain(pair: Pair<String, Float>): Float = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<String, Double>): Double = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<String, Byte>): Byte = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<String, Int>): Int = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<String, Long>): Long = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<String, Short>): Short = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<String, Boolean>): Boolean = obtain(pair.first, pair.second)

    fun obtain(pair: Pair<String, String>): String = obtain(pair.first, pair.second)



    fun obtainVariable(name: String, defaultValue: String = ""): Variable =
        variables.getOrPut(name) { StringVariable(defaultValue) }

    fun obtainVariable(name: String, defaultValue: Float = 0F): Variable =
        variables.getOrPut(name) { FloatVariable(defaultValue) }

    fun obtainVariable(name: String, defaultValue: Double = 0.0): Variable =
        variables.getOrPut(name) { DoubleVariable(defaultValue) }

    fun obtainVariable(name: String, defaultValue: Int = 0): Variable =
        variables.getOrPut(name) { IntVariable(defaultValue) }

    fun obtainVariable(name: String, defaultValue: Long = 0L): Variable =
        variables.getOrPut(name) { LongVariable(defaultValue) }

    fun obtainVariable(name: String, defaultValue: Short = 0): Variable =
        variables.getOrPut(name) { ShortVariable(defaultValue) }

    fun obtainVariable(name: String, defaultValue: Byte = 0): Variable =
        variables.getOrPut(name) { ByteVariable(defaultValue) }

    fun obtainVariable(name: String, defaultValue: Boolean = false): Variable =
        variables.getOrPut(name) { BooleanVariable(defaultValue) }



    fun obtainVariable(pair: Pair<String, Any>): Variable =
        variables.getOrPut(pair.first) { obtain(pair.second) }



    fun put(other: ImmutableVariables) {
        other.asMap().forEach { (k, v) -> variables[k] = v.copy() }
    }

    fun put(name: String, value: Double) {
        variables[name] = DoubleVariable(value)
    }

    fun put(name: String, value: Float) {
        variables[name] = FloatVariable(value)
    }

    fun put(name: String, value: Int) {
        variables[name] = IntVariable(value)
    }

    fun put(name: String, value: Long) {
        variables[name] = LongVariable(value)
    }

    fun put(name: String, value: Short) {
        variables[name] = ShortVariable(value)
    }

    fun put(name: String, value: Byte) {
        variables[name] = ByteVariable(value)
    }

    fun put(name: String, value: Boolean) {
        variables[name] = BooleanVariable(value)
    }

    fun put(name: String, value: String) {
        variables[name] = StringVariable(value)
    }



    fun set(other: ImmutableVariables) {
        other.asMap().forEach { (k, v) ->
            val variable = variables[k]
            if (variable == null)
                variables[k] = v.copy() else
                variable.set(v.value)
        }
    }

    fun set(name: String, value: Double) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = DoubleVariable(value) else
            variable.set(value)
    }

    fun set(name: String, value: Float) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = FloatVariable(value) else
            variable.set(value)
    }

    fun set(name: String, value: Int) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = IntVariable(value) else
            variable.set(value)
    }

    fun set(name: String, value: Long) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = LongVariable(value) else
            variable.set(value)
    }

    fun set(name: String, value: Short) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = ShortVariable(value) else
            variable.set(value)
    }

    fun set(name: String, value: Byte) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = ByteVariable(value) else
            variable.set(value)
    }

    fun set(name: String, value: Boolean) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = BooleanVariable(value) else
            variable.set(value)
    }

    fun set(name: String, value: String) {
        val variable = variables[name]
        if (variable == null)
            variables[name] = StringVariable(value) else
            variable.set(value)
    }



    fun set(pair: Pair<String, String>, value: String = pair.second) = set(pair.first, value)

    fun set(pair: Pair<String, Float>, value: Float = pair.second) = set(pair.first, value)

    fun set(pair: Pair<String, Boolean>, value: Boolean = pair.second) = set(pair.first, value)

    fun set(pair: Pair<String, Byte>, value: Byte = pair.second) = set(pair.first, value)

    fun set(pair: Pair<String, Short>, value: Short = pair.second) = set(pair.first, value)

    fun set(pair: Pair<String, Double>, value: Double = pair.second) = set(pair.first, value)

    fun set(pair: Pair<String, Int>, value: Int = pair.second) = set(pair.first, value)

    fun set(pair: Pair<String, Long>, value: Long = pair.second) = set(pair.first, value)



    fun remove(name: String): ImmutableVariable? = variables.remove(name)

    fun clear() {
        variables.values.forEach { it.invalidate() }
        variables.clear()
    }

    fun addListeners(target: Any) {
        for (method in target.javaClass.declaredMethods) {
            val annotation = method.getDeclaredAnnotation(ChangeListener::class.java) ?: continue
            if (method.parameterCount != 1) continue
            variables.getOrPut(annotation.value.ifEmpty { method.name }) { obtain(method.parameterTypes[0]) }
                .addListener(MethodListener(target, method))
        }
    }



    override fun inject(target: Any) {
        for (field in target.javaClass.declaredFields) {
            val variable = variables[field.name] ?: continue
            val value: Any = when (field.type) {
                Float::class.java   -> variable.toFloat()
                Double::class.java  -> variable.toDouble()

                Int::class.java     -> variable.toInt()
                Short::class.java   -> variable.toShort()
                Long::class.java    -> variable.toLong()
                Byte::class.java    -> variable.toByte()

                Boolean::class.java -> variable.toBoolean()
                String::class.java  -> variable.toString()

                else -> continue
            }
            field.isAccessible = true
            field.set(target, value)
        }
    }

    override fun asMap(): Map<String, Variable> = variables

    override fun getVariable(name: String): Variable? = variables[name]



    companion object {

        private val variableFactory = IdentityHashMap<Class<*>, Constructor<out Variable>>()



        init {
            variableFactory[java.lang.Float::class.java] = FloatVariable::class.java.getDeclaredConstructor()
            variableFactory[java.lang.Double::class.java] = DoubleVariable::class.java.getDeclaredConstructor()

            variableFactory[java.lang.Integer::class.java] = IntVariable::class.java.getDeclaredConstructor()
            variableFactory[java.lang.Short::class.java] = ShortVariable::class.java.getDeclaredConstructor()
            variableFactory[java.lang.Long::class.java] = LongVariable::class.java.getDeclaredConstructor()
            variableFactory[java.lang.Byte::class.java] = ByteVariable::class.java.getDeclaredConstructor()

            variableFactory[java.lang.Boolean::class.java] = BooleanVariable::class.java.getDeclaredConstructor()
            variableFactory[java.lang.String::class.java] = StringVariable::class.java.getDeclaredConstructor()
        }



        fun obtain(value: Any): Variable =
            obtain(value.javaClass).also { it.set(value) }

        fun obtain(type: Class<*>): Variable = (variableFactory[type]
            ?: throw IllegalStateException("Bad variable type ($type)!")).newInstance()
    }
}