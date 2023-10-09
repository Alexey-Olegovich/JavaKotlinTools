package alexey.tools.common.resources

import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLConnection

open class URLResource(val url: URL): ResourceBase() {

    constructor(url: String) : this(URI(url).toURL())



    override fun getResource(relativePath: String): Resource = URLResource(URL(url, relativePath))
    override fun getResourceType(): String = "url"
    override fun toURL(): URL = url
    override fun getPath(): String = url.toString()
    override fun getInputStream(): InputStream = url.openConnection().getInputStream()
    override fun getOutputStream(): OutputStream = url.openConnection().let { it.doOutput = true; it.getOutputStream() }

    override fun canRead(): Boolean {
        try {
            return (checkConnection() ?: return false).contentLengthLong > 0
        } catch (_: Throwable) {
            return false
        }
    }

    override fun length() = try {
        checkConnection()?.contentLengthLong ?: -1
    } catch (_: Throwable) {
        -1L
    }

    override fun getContentType(): String {
        try {
            val connection = checkConnection() ?: return super.getContentType()
            return connection.contentType ?: super.getContentType()
        } catch (_: Throwable) {
            return super.getContentType()
        }
    }



    protected open fun checkConnection(): URLConnection? {
        val connection = url.openConnection()
        connection.useCaches = false
        if (connection !is HttpURLConnection) return connection
        connection.requestMethod = "HEAD"
        if (connection.responseCode < HttpURLConnection.HTTP_BAD_REQUEST) return connection
        connection.disconnect()
        return null
    }
}