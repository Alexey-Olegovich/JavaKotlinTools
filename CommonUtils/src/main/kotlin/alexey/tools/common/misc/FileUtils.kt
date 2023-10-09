package alexey.tools.common.misc

import alexey.tools.common.collections.ObjectList
import java.io.File

private fun File.listAllFiles(result: ObjectList<File>): ObjectList<File> {
    if (isDirectory)
        listFiles()?.forEach {
            if (it.isDirectory)
                it.listAllFiles(result) else
                result.add(it)
        }
    return result
}

private fun File.listAllFiles(result: ObjectList<File>, extensions: Array<out String>): ObjectList<File> {
    if (isDirectory)
        listFiles()?.forEach {
            if (it.isDirectory)
                it.listAllFiles(result, extensions) else
                for (extension in extensions)
                    if (it.path.endsWith(extension, true)) {
                        result.add(it)
                        break
                    }
        }
    return result
}

private fun File.listAllFiles(result: ObjectList<File>, extension: String): ObjectList<File> {
    if (isDirectory)
        listFiles()?.forEach {
            if (it.isDirectory)
                it.listAllFiles(result, extension) else
                if (it.path.endsWith(extension, true))
                    result.add(it)
        }
    return result
}

private fun File.listAllEntries(result: ObjectList<File>, regex: Regex): ObjectList<File> {
    if (isDirectory)
        listFiles()?.forEach {
            if (regex.containsMatchIn(it.invariantSeparatorsPath)) result.add(it)
            if (it.isDirectory) it.listAllEntries(result, regex)
        }
    return result
}

private fun File.listAllEntries(result: ObjectList<File>, contains: String): ObjectList<File> {
    if (isDirectory)
        listFiles()?.forEach {
            if (it.invariantSeparatorsPath.contains(contains)) result.add(it)
            if (it.isDirectory) it.listAllEntries(result, contains)
        }
    return result
}



fun File.listAllEntries(contains: String): List<File> = listAllEntries(ObjectList(), contains)

fun File.listAllEntries(regex: Regex): List<File> = listAllEntries(ObjectList(), regex)

fun File.listJustFiles(extension: String): List<File> {
    if (!isDirectory) return emptyList()
    val array = listFiles() ?: return emptyList()
    val result = ObjectList<File>()
    array.forEach { if (!it.isDirectory && it.path.endsWith(extension, true)) result.add(it) }
    return result
}

fun File.listJustFiles(extensions: Array<out String>): List<File> {
    if (!isDirectory) return emptyList()
    val array = listFiles() ?: return emptyList()
    val result = ObjectList<File>()
    array.forEach {
        if (!it.isDirectory)
            for (extension in extensions)
                if (it.path.endsWith(extension, true)) {
                    result.add(it)
                    break
                }
    }
    return result
}

fun File.listJustFiles(): List<File> {
    if (!isDirectory) return emptyList()
    val array = listFiles() ?: return emptyList()
    val result = ObjectList<File>()
    array.forEach { if (!it.isDirectory) result.add(it) }
    return result
}



fun File.listJustEntries(extensions: Array<out String>): List<String> {
    if (!isDirectory) return emptyList()
    val array = list() ?: return emptyList()
    val result = ObjectList<String>()
    array.forEach {
        for (extension in extensions)
            if (it.endsWith(extension, true)) {
                result.add(path + '\\' + it)
                break
            }
    }
    return result
}

fun File.listJustEntries(extension: String): List<String> {
    if (!isDirectory) return emptyList()
    val array = list() ?: return emptyList()
    val result = ObjectList<String>()
    array.forEach {
        if (it.endsWith(extension, true)) result.add(path + '\\' + it)
    }
    return result
}

fun File.listJustEntries(): List<String> {
    if (!isDirectory) return emptyList()
    val array = list() ?: return emptyList()
    val result = ObjectList<String>()
    array.forEach { result.add(path + '\\' + it) }
    return result
}



fun listJustEntries(directory: String): List<String> = File(directory).listJustEntries()

fun listJustEntries(directory: String, extension: String): List<String> = File(directory).listJustEntries(extension)

fun listJustEntries(directory: String, vararg extensions: String): List<String> = File(directory).listJustEntries(extensions)



fun listJustFiles(directory: String): List<File> = File(directory).listJustFiles()

fun listJustFiles(directory: String, extension: String): List<File> = File(directory).listJustFiles(extension)

fun listJustFiles(directory: String, vararg extensions: String): List<File> = File(directory).listJustFiles(extensions)



fun listAllFiles(directory: String): List<File> = File(directory).listAllFiles()

fun listAllFiles(directory: String, extension: String): List<File> = File(directory).listAllFiles(extension)

fun listAllFiles(directory: String, vararg extensions: String): List<File> = File(directory).listAllFiles(ObjectList(), extensions)

fun File.listAllFiles(): List<File> = listAllFiles(ObjectList())

fun File.listAllFiles(extension: String): List<File> = listAllFiles(ObjectList(), extension)

fun File.listAllFiles(vararg extensions: String): List<File> = listAllFiles(ObjectList(), extensions)