package alexey.tools.common.misc

import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

fun URL.readData(proxy: Proxy? = null): ByteArray {
    val connection = if (proxy == null) openConnection() else openConnection(proxy)
    val length = connection.contentLength
    if (length < 1) return ByteArray(0)
    val input = connection.getInputStream()
    try {
        val data = ByteArray(length)
        var offset = 0
        var read = input.read(data)
        while (read >= 0) {
            offset += read
            read = input.read(data, offset, length - offset)
        }
        return data
    } finally {
        input.close()
    }
}

fun httpProxy(ip: String, port: Int) = Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(ip, port))

fun Proxy.isWorking(url: URL, timeout: Int = 4000) = try {
    val connection = url.openConnection(this)
    connection.connectTimeout = timeout
    connection.connect()
    true
} catch (e: Throwable) {
    false
}

