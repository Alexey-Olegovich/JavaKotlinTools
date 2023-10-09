package alexey.tools.common.resources

import alexey.tools.common.misc.getName
import java.io.*
import java.net.URL

interface Resource {
    fun getResource(relativePath: String): Resource = NULL

    fun getOutputStream(): OutputStream = throw UnsupportedOperationException("getOutputStream")
    fun getInputStream(): InputStream = throw UnsupportedOperationException("getInputStream")

    fun canRead(): Boolean = try { getInputStream().close(); true } catch (_: Throwable) { false }
    fun canWrite(): Boolean = try { getOutputStream().close(); true } catch (_: Throwable) { false }
    fun length(): Long = -1L

    fun getPath(): String = ""
    fun getName(): String = getPath().getName()
    fun getResourceType(): String = "undefined"
    fun getContentType(): String = "application/octet-stream"

    fun list(): List<Resource> = emptyList()
    fun isValid() = true
    fun toURL(): URL = URL("resource", "", -1, toString(), ResourceURLStreamHandler(this))

    companion object {
        val NULL = object : ResourceBase() {
            override fun canRead(): Boolean = false
            override fun canWrite(): Boolean = false
            override fun getResourceType(): String = "null"
            override fun toString(): String = "null:"
            override fun isValid(): Boolean = false
        }
    }
}