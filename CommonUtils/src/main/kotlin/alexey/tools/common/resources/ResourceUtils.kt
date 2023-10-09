package alexey.tools.common.resources

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.misc.halve
import alexey.tools.common.misc.silentTry
import alexey.tools.common.misc.tryClose
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.util.function.BiConsumer

fun Resource.readText(): String = getInputStream().reader().tryClose(Reader::readText)

fun Resource.readBytes(): ByteArray = getInputStream().tryClose(InputStream::readBytes)

fun Resource.readBytes(length: Int): ByteArray {
    getInputStream().tryClose {
        val result = ByteArray(length)
        var offset = 0
        while (offset < length) {
            val amount = read(result, offset, length - offset)
            if (amount < 0) return result
            offset += amount
        }
        return result
    }
}

fun Resource.readBytes(file: File) {
    getInputStream().use { i ->
        file.outputStream().use { o ->
            i.copyTo(o)
        }
    }
}

inline fun Resource.withReadableResource(path: String, action: (Resource) -> Unit) =
    getResource(path).let { if (it.canRead()) action(it) }

inline fun Resource.withInputStream(path: String, action: (InputStream) -> Unit) =
    withValidResource(path) { silentTry { action(getInputStream()) } }

inline fun Resource.withValidResource(path: String, action: Resource.() -> Unit) =
    getResource(path).let { if (it.isValid()) action(it) }

fun Resource.readConfig(): List<Pair<String, String>> {
    BufferedReader(InputStreamReader(getInputStream())).tryClose {
        val result = ObjectList<Pair<String, String>>()
        var line = readLine()
        while (line != null) {
            if (!line.isBlankLine())
                result.add(line.replace(whitespaceRemovePattern, "").halve('='))
            line = readLine()
        }
        return result
    }
}

fun BiConsumer<String, String>.readConfig(resource: Resource) {
    BufferedReader(InputStreamReader(resource.getInputStream())).tryClose {
        var line = readLine()
        while (line != null) {
            if (!line.isBlankLine()) {
                line = line.replace(whitespaceRemovePattern, "")
                val index = line.indexOf('=')
                if (index == -1)
                    accept(line, "") else
                    accept(line.substring(0, index), line.substring(index + 1))
            }
            line = readLine()
        }
    }
}

private fun String.isBlankLine() = isEmpty() || all { it.isWhitespace() || it == '=' }

private val whitespaceRemovePattern = "\\s".toRegex()

fun Resource.toURL(path: String) = URL("resource", "", -1, path, ResourceURLStreamHandler(this))