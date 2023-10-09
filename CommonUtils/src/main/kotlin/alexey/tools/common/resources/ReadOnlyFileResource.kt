package alexey.tools.common.resources

import alexey.tools.common.collections.convert
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files

open class ReadOnlyFileResource(protected val file: File) : ResourceBase() {
    constructor(file: String) : this(File(file))
    override fun getResource(relativePath: String): Resource =
        ReadOnlyFileResource(File(if (file.isDirectory) file.path else file.parent, relativePath))
    override fun getPath(): String = file.invariantSeparatorsPath
    override fun getResourceType(): String = "file"
    override fun getInputStream(): InputStream = file.inputStream()
    override fun length(): Long = file.length()
    override fun canRead(): Boolean = file.canRead()
    override fun canWrite(): Boolean = false
    override fun getName(): String = file.name
    override fun toURL(): URL = file.toURI().toURL()
    override fun getContentType(): String = Files.probeContentType(file.toPath()) ?: super.getContentType()
    override fun list(): List<Resource> {
        val files = file.list() ?: return emptyList()
        if (files.isEmpty()) return emptyList()
        return files.convert { ReadOnlyFileResource(File(file, it)) }
    }
}