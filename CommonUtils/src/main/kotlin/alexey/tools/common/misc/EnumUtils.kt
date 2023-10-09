package alexey.tools.common.misc

fun <T : Enum<T>> String.toEnum(clazz: Class<T>): T = java.lang.Enum.valueOf(clazz, this)

fun <T : Enum<T>> maxId(clazz: Class<T>): Int = clazz.enumConstants.size