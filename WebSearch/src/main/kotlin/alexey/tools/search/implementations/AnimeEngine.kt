package alexey.tools.search.implementations

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.misc.MiscUtils
import alexey.tools.search.core.DefaultSearchResult
import alexey.tools.search.core.HttpSearchEngine
import alexey.tools.search.core.SearchEngine
import alexey.tools.search.core.SearchResult
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.Proxy

class AnimeEngine(proxy: Proxy? = null,
                  userAgent: String = SearchEngine.DESKTOP_USER_AGENT,
                  private val baseUrl: String = "https://nyaa.si"): HttpSearchEngine(baseUrl, proxy, userAgent) {

    override fun search(query: String): Iterator<SearchResult> {
        val doc = newRequest().data("q", query).execute().parse()
        return Results(getResults(doc), nextPage(doc))
    }

    override fun name(): String = baseUrl



    private fun nextPage(doc: Document): String? {
        val elements = doc.getElementsByClass("next")
        if (elements.isEmpty()) return null
        val href = elements[0].child(0).attr("href")
        return href.ifEmpty { null }
    }

    private fun getResults(doc: Document): Iterator<Element> =
        doc.getElementsByTag("table")[0].child(1).children().iterator()



    private inner class Results(private var results: Iterator<Element>,
                                private var nextUrl: String? = null): Iterator<SearchResult> {

        private val lock = MiscUtils.newObject()



        override fun hasNext() = synchronized(lock) {
            if (results.hasNext()) true else nextUrl != null
        }

        override fun next(): SearchResult = getResult(synchronized(lock) {
            if (!results.hasNext()) {
                val doc = newRequest()
                    .url(baseUrl + (nextUrl ?: throw NoSuchElementException()))
                    .execute().parse()
                results = getResults(doc)
                nextUrl = nextPage(doc)
            }
            results.next()
        })



        private fun getResult(row: Element): SearchResult {
            val columns = row.children()
            val tags = ObjectList<String>()
            tags.unsafeAdd("Size: " + columns[3].text())
            tags.unsafeAdd("Date: " + columns[4].text())
            tags.unsafeAdd("Seeders: " + columns[5].text())
            tags.unsafeAdd("Leechers: " + columns[6].text())
            tags.unsafeAdd("Downloads: " + columns[7].text())
            return DefaultSearchResult(columns[1].text(),
                createResource(baseUrl + columns[2].child(0).attr("href")),
                createResource(baseUrl + columns[0].child(0).child(0).attr("src")), "", tags)
        }
    }
}