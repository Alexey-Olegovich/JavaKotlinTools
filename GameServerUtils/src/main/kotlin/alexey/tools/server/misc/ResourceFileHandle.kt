package alexey.tools.server.misc

import alexey.tools.common.resources.Resource
import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import java.io.*

class ResourceFileHandle(val resource: Resource): FileHandle() {

    init {
        file = File(resource.getPath())
        type = if (resource.getResourceType().startsWith("file"))
            FileType.Absolute else FileType.Classpath
    }

    override fun file(): File = file

    override fun path(): String = resource.getPath()

    override fun name(): String = file.path.run { substring(lastIndexOf(File.separatorChar) + 1) }

    override fun extension(): String =
        file.path.run { lastIndexOf('.').let { if (it == -1) "" else substring(it + 1) } }

    override fun nameWithoutExtension(): String = name().removeExtension()

    override fun pathWithoutExtension(): String = path().removeExtension()

    override fun read(): InputStream = resource.getInputStream()

    override fun write(append: Boolean): OutputStream = resource.getOutputStream()

    override fun writer(append: Boolean, charset: String?): Writer =
        try {
            val output = resource.getOutputStream()
            if (charset == null)
                OutputStreamWriter(output) else
                OutputStreamWriter(output, charset)
        } catch (ex: IOException) {
            throw GdxRuntimeException("Error writing file: $file ($type)", ex)
        }

    override fun child(name: String): FileHandle = ResourceFileHandle(resource.getResource(name))

    override fun sibling(name: String): FileHandle = child("../$name")

    override fun parent(): FileHandle = child("..")

    override fun mkdirs() { if (isFile()) file.mkdirs() else
        throw GdxRuntimeException("Cannot mkdirs with a classpath file: $file") }

    override fun exists(): Boolean = if (!resource.isValid()) false else
        if (isFile()) file.exists() else resource.canRead()

    override fun delete(): Boolean = if (isFile()) file.delete() else
        throw GdxRuntimeException("Cannot delete a classpath file: $file")

    override fun moveTo(dest: FileHandle) {
        if (isFile() && file.renameTo(dest.file())) return
        copyTo(dest); delete()
        if (exists() && isDirectory) deleteDirectory()
    }

    override fun length(): Long {
        if (isFile()) return file().length()
        val length = resource.length()
        return if (length > 0) length else 0
    }



    private fun String.removeExtension() = lastIndexOf('.').let { if (it == -1) this else substring(0, it) }

    private fun isFile(): Boolean = type === FileType.Absolute
}