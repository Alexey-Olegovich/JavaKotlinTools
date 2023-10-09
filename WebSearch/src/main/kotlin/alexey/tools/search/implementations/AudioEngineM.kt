package alexey.tools.search.implementations

import alexey.tools.common.resources.AdvancedURLResource
import alexey.tools.search.core.DefaultSearchResult
import alexey.tools.search.core.HttpSearchEngine
import alexey.tools.search.core.SearchEngine
import alexey.tools.search.core.SearchResult
import org.jsoup.nodes.Element
import java.net.Proxy

class AudioEngineM(proxy: Proxy? = null,
                   userAgent: String = SearchEngine.DESKTOP_USER_AGENT,
                   private val base: String = "https://ruo.morsmusic.org",
                   private val search: String = "$base/search/"): HttpSearchEngine(null, proxy, userAgent) {

    override fun search(query: String): Iterator<SearchResult> =
        Results(newRequest().url(search + query).get().getElementsByClass("track mustoggler __adv_list_track").iterator())

    override fun name() = base



    private inner class Results(private val results: Iterator<Element>): Iterator<SearchResult> {

        override fun hasNext() = synchronized(results) { results.hasNext() }

        override fun next() = getResult(synchronized(results) { results.next() })



        private fun getResult(element: Element): SearchResult {
            val div = element.child(0).attr("style")
            return DefaultSearchResult(
                element.child(1).child(0).text(),
                AdvancedURLResource(base + element.getElementsByClass("track-download")[0].attr("href"), getProxy()),
                createResource(div.substring(23, div.length - 2)))
        }
    }
}