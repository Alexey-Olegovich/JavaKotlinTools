package alexey.tools.common.resources

import alexey.tools.common.converters.PartialURLInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URI
import java.net.URL
import java.net.URLConnection

open class AdvancedURLResource(url: URL,
                               val proxy: Proxy? = null,
                               val headers: Map<String, String> = emptyMap()): URLResource(url) {

    constructor(url: String, proxy: Proxy? = null, headers: Map<String, String> = emptyMap()):
            this(URI(url).toURL(), proxy, headers)



    fun openConnection(start: Long = 0L): URLConnection = openConnection(url, start)



    override fun toURL(): URL = toURL(getPath())
    override fun getResource(relativePath: String): Resource = AdvancedURLResource(URL(url, relativePath), proxy, headers)
    override fun getInputStream(): InputStream = PartialURLInputStream(this)
    override fun getOutputStream(): OutputStream = openConnection().let { it.doOutput = true; it.getOutputStream() }



    override fun checkConnection(): URLConnection? = checkConnection(url)

    private fun checkConnection(url: URL): URLConnection? {
        val connection = openConnection(url)
        connection.useCaches = false
        if (connection !is HttpURLConnection) return connection
        connection.requestMethod = "HEAD"
        val status = connection.responseCode
        if (status in 301..303) return checkConnection(URI(connection.getHeaderField("Location")).toURL())
        if (connection.responseCode < HttpURLConnection.HTTP_BAD_REQUEST) return connection
        connection.disconnect()
        return null
    }

    private fun openConnection(url: URL): URLConnection {
        val connection = if (proxy == null)
            url.openConnection() else
            url.openConnection(proxy)
        headers.forEach { (k, v) -> connection.addRequestProperty(k, v) }
        return connection
    }

    private fun openConnection(url: URL, start: Long): URLConnection {
        val connection = openConnection(url)
        if (start > 0) connection.addRequestProperty("Range", "bytes=$start-")
        if (connection !is HttpURLConnection) return connection
        val status = connection.responseCode
        return if (status in 301..303)
            openConnection(URI(connection.getHeaderField("Location")).toURL(), start) else
            connection
    }



    companion object {
        fun newInstance(url: URL,
                        proxy: Proxy? = null,
                        cookies: String? = null,
                        userAgent: String? = null,
                        accept: String? = "*/*"): AdvancedURLResource =
            AdvancedURLResource(url, proxy, newHeaders(cookies, userAgent, accept))

        fun newInstance(url: String,
                        proxy: Proxy? = null,
                        cookies: String? = null,
                        userAgent: String? = null,
                        accept: String? = "*/*"): AdvancedURLResource =
            newInstance(URI(url).toURL(), proxy, cookies, userAgent, accept)

        fun newHeaders(cookies: String? = null,
                       userAgent: String? = null,
                       accept: String? = "*/*"): Map<String, String> {
            val headers = HashMap<String, String>()
            if (cookies != null) headers["Cookie"] = cookies
            if (userAgent != null) headers["User-Agent"] = userAgent
            if (accept != null) headers["Accept"] = accept
            return headers
        }
    }
}