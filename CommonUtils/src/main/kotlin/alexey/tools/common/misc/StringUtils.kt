package alexey.tools.common.misc

import alexey.tools.common.collections.ObjectList

fun String.deleteChar(char: Char): String = deleteChar(indexOf(char))

fun String.deleteLastChar(char: Char): String = deleteChar(lastIndexOf(char))

private fun String.deleteChar(index: Int): String {
    if (index == -1) return this
    val sb = StringBuilder(length - 1)
    sb.append(this, 0, index)
    sb.append(this, index + 1, length)
    return sb.toString()
}

fun String.regionMatches(thisOffset: Int, other: String): Boolean =
    this.regionMatches(thisOffset, other, 0, other.length)

fun String.toLowercaseWords() = buildString {
    for (c in this@toLowercaseWords) {
        if (c in 'A'..'Z') {
            if (isNotEmpty()) append('_')
            append(c.lowercaseChar())
        } else append(c)
    }
}

fun StringBuilder.appendLowercaseWords(value: String, removePostfix: Int) {
    val end = value.length - removePostfix
    var i = 0
    while (i < end) {
        val c = value[i++]
        if (c in 'A'..'Z') {
            if (i != 1) append('_')
            append(c.lowercaseChar())
        } else append(c)
    }
}

fun String.halve(delimiter: Char): Pair<String, String> = halve(indexOf(delimiter))

fun String.halve(index: Int): Pair<String, String> =
    if (index == -1)
        Pair(this, "") else
        Pair(substring(0, index), substring(index + 1))

fun String.split(delimiter: Char): List<String> {
    var currentOffset = 0
    var nextIndex = indexOf(delimiter, currentOffset)
    if (nextIndex == -1) return listOf(this)
    val result = ObjectList<String>(4)
    do {
        result.add(substring(currentOffset, nextIndex))
        currentOffset = nextIndex + 1
        nextIndex = indexOf(delimiter, currentOffset)
    } while (nextIndex != -1)
    result.add(substring(currentOffset, length))
    return result
}

fun String.contains(array: Array<String>): Boolean {
    for (e in array) if (contains(e)) return true
    return false
}

fun String.decapitalize(): String {
    val a = toCharArray()
    a[0] = Character.toLowerCase(a[0])
    return String(a)
}

infix fun String.add(o: String) =
    if (isEmpty()) o else if (o.isEmpty()) this else this + o