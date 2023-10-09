package alexey.tools.search.core

import alexey.tools.common.resources.AdvancedURLResource
import alexey.tools.common.resources.Resource
import alexey.tools.common.resources.URLResource
import org.jsoup.Connection
import org.jsoup.Connection.Method
import org.jsoup.Jsoup
import java.net.Proxy

abstract class HttpSearchEngine(sessionUrl: String? = null,
                                @Volatile private var proxy: Proxy? = null,
                                userAgent: String = SearchEngine.DESKTOP_USER_AGENT,
                                method: Method = Method.GET): ProxySearchEngine {

    private val session = Jsoup
        .newSession()
        .proxy(proxy)
        .userAgent(userAgent)
        .method(method)

    init {
        if (sessionUrl != null) session.url(sessionUrl)
    }



    protected fun newRequest(): Connection = synchronized(session) { session.newRequest() }

    protected open fun createResource(url: String): Resource {
        val proxy = proxy
        return if (proxy == null) URLResource(url) else AdvancedURLResource(url, proxy)
    }

    protected fun setCookies(cookies: Map<String, String>) {
        synchronized(session) {
            session.cookies(cookies)
        }
    }

    protected fun setCookie(key: String, value: String) {
        synchronized(session) {
            session.cookie(key, value)
        }
    }



    override fun setProxy(proxy: Proxy?) {
        this.proxy = proxy
        synchronized(session) {
            session.proxy(proxy)
        }
    }

    override fun getProxy() = proxy
}