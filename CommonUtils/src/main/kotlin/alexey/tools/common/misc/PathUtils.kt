package alexey.tools.common.misc

fun String.removeExtension(c: Char = '.'): String {
    val index = lastIndexOf(c)
    return if (index == -1) this else substring(0, index)
}

fun String.getExtension(c: Char = '.'): String {
    val index = lastIndexOf(c)
    return if (index == -1) this else substring(index)
}

fun String.getExtensionText(c: Char = '.'): String {
    val index = lastIndexOf(c)
    return if (index == -1) this else substring(index + 1)
}

fun String.getName(s: Char = '/'): String {
    val end = lastIndexOf(s)
    if (end == -1) return this
    return if (end == lastIndex) {
        if (end == 0) return ""
        val begin = lastIndexOf(s, end - 1)
        substring(begin + 1, end)
    } else {
        substring(end + 1)
    }
}

fun String.objectNameToPath(extension: String = ".class"): String =
    replace('.', '/') + extension
