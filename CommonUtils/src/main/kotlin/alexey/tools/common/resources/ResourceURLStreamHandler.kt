package alexey.tools.common.resources

import java.net.ProtocolException
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler

class ResourceURLStreamHandler(private val resource: Resource): URLStreamHandler() {

    override fun openConnection(u: URL) = ResourceURLConnection(u, resource)



    class ResourceURLConnection(url: URL, private val resource: Resource) : URLConnection(url) {

        override fun connect() {}

        override fun getInputStream() = if (doInput) resource.getInputStream() else
            throw ProtocolException("doInput=false - call setDoInput(true)")

        override fun getOutputStream() = if (doOutput) resource.getOutputStream() else
            throw ProtocolException("doOutput=false - call setDoOutput(true)")
    }
}