package alexey.tools.search.core

import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.Proxy

fun Proxy.isWorking(url: String, timeout: Int = 4000, userAgent: String = SearchEngine.DESKTOP_USER_AGENT) =
    try {
        Jsoup.connect(url).userAgent(userAgent).timeout(timeout).proxy(this).execute()
            .statusCode() < HttpURLConnection.HTTP_BAD_REQUEST
    } catch (_: Throwable) {
        false
    }

fun createCookies(cookies: Map<String, String>): String =
    buildString { cookies.forEach { (t, u) -> append(t).append('=').append(u).append(';') } }
