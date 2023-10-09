package alexey.tools.search.core

import java.net.Proxy

interface ProxySearchEngine: SearchEngine {
    fun setProxy(proxy: Proxy?)
    fun getProxy(): Proxy?
}