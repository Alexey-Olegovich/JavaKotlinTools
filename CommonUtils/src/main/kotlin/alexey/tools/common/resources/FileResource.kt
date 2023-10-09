package alexey.tools.common.resources

import alexey.tools.common.collections.convert
import java.io.*

open class FileResource(file: File) : ReadOnlyFileResource(file) {
    constructor(file: String = ".") : this(File(file))
    override fun getResource(relativePath: String): Resource =
        FileResource(File(if (file.isDirectory) file.path else file.parent, relativePath))
    override fun getOutputStream(): OutputStream = file.run { parentFile?.mkdirs(); outputStream() }
    override fun canWrite(): Boolean = !file.exists() || file.canWrite()
    override fun list(): List<Resource> {
        val files = file.list() ?: return emptyList()
        if (files.isEmpty()) return emptyList()
        return files.convert { FileResource(File(file, it)) }
    }
}