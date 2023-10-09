package alexey.tools.common.resources

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.misc.PathUtils.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZipFileResource(file: File) : ReadOnlyFileResource(file), ResourceRoot {

    val zipFile = ZipFile(file)

    constructor(file: String) : this(File(file))



    override fun getResource(relativePath: String): Resource {
        val path = normalizePath(relativePath)
        if (path.isEmpty()) return this
        val entry = zipFile.getEntry(path)
        return if (entry == null) Resource.NULL else ZipEntryResource(entry)
    }

    override fun close() = zipFile.close()
    override fun getResourceType(): String = "file-zip"
    override fun length(): Long = file.length()

    override fun list(): List<Resource> {
        val entries = zipFile.entries()
        val list = ObjectList<Resource>()
        while (entries.hasMoreElements()) list.add(ZipEntryResource(entries.nextElement()))
        return list
    }



    private inner class ZipEntryResource(private val zipEntry: ZipEntry) : ResourceBase() {

        override fun getResource(relativePath: String): Resource {
            if (relativePath.isEmpty()) return this
            return this@ZipFileResource.getResource(concatenatePaths(zipEntry.name, relativePath))
        }

        override fun getInputStream(): InputStream = zipFile.getInputStream(zipEntry)
        override fun getPath(): String = file.invariantSeparatorsPath + "!/" + zipEntry.name
        override fun getName(): String = zipEntry.name
        override fun getResourceType(): String = "file-zip-entry"
        override fun length(): Long = zipEntry.size
        override fun canRead(): Boolean = true
        override fun canWrite(): Boolean = false
        //override fun toURL(): URL = toURL("jar:file:" + getPath())
        override fun getContentType(): String =
            Files.probeContentType(Paths.get(zipEntry.name)) ?: super.getContentType()
    }
}