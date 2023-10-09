package alexey.tools.search.implementations

import alexey.tools.common.resources.AdvancedURLResource
import alexey.tools.common.resources.Resource
import alexey.tools.search.core.DefaultSearchResult
import alexey.tools.search.core.HttpSearchEngine
import alexey.tools.search.core.SearchEngine
import alexey.tools.search.core.SearchResult
import org.jsoup.nodes.Element
import java.net.Proxy

class AudioEngineP(proxy: Proxy? = null,
                   userAgent: String = SearchEngine.DESKTOP_USER_AGENT,
                   private val base: String = "https://mp3party.net/",
                   search: String = base + "search"): HttpSearchEngine(search, proxy, userAgent) {

    override fun search(query: String): Iterator<SearchResult> =
        Results(newRequest().data("q", query).get().getElementsByClass("track__user-panel").iterator())

    override fun name() = base



    private inner class Results(private val results: Iterator<Element>): Iterator<SearchResult> {

        override fun hasNext() = synchronized(results) { results.hasNext() }

        override fun next() = getResult(synchronized(results) { results.next() })



        private fun getResult(element: Element): SearchResult {
            val image = element.attr("data-js-image")
            return DefaultSearchResult(
                element.attr("data-js-song-title"),
                AdvancedURLResource(element.attr("data-js-url"), getProxy()),
                if (image.isEmpty()) Resource.NULL else createResource("https://i.mp3party.net$image"))
        }
    }

}