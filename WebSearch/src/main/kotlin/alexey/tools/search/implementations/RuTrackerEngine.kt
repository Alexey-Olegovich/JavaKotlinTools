package alexey.tools.search.implementations

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.resources.AdvancedURLResource
import alexey.tools.common.resources.Resource
import alexey.tools.search.core.HttpSearchEngine
import alexey.tools.search.core.PrivateSearchEngine
import alexey.tools.search.core.SearchEngine.Companion.DESKTOP_USER_AGENT
import alexey.tools.search.core.SearchResult
import alexey.tools.search.core.createCookies
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.Proxy

class RuTrackerEngine(proxy: Proxy? = null,
                      val userAgent: String = DESKTOP_USER_AGENT,
                      private val baseUrl: String = "https://rutracker.org/",
                      private val forumUrl: String = baseUrl + "forum/",
                      private val loginUrl: String = forumUrl + "login.php",
                      trackerUrl: String = forumUrl + "tracker.php"): HttpSearchEngine(trackerUrl, proxy, userAgent),
    PrivateSearchEngine {

    @Volatile private var cookies = ""



    override fun login(user: String, password: String) {
        val enter = Jsoup
            .connect(loginUrl)
            .proxy(getProxy())
            .userAgent(userAgent)
            .method(Connection.Method.POST)
            .data("login_username", user)
            .data("login_password", password)
            .data("login", user)
            .execute()

        val cookies = enter.cookies()
        setCookies(cookies)
        this.cookies = createCookies(cookies)
    }

    fun getCookies() = cookies



    override fun name(): String = baseUrl

    override fun search(query: String): Iterator<SearchResult> {
        val table = newRequest()
            .data("nm", query)
            .execute()
            .parse()
            .getElementById("tor-tbl") ?: return emptyList<SearchResult>().iterator()
        val tableBody = table.child(1)
        if (tableBody.child(0).child(0).attr("colspan") == "10")
            return emptyList<SearchResult>().iterator()
        return Results(tableBody.children().iterator())
    }



    private inner class Results(private val results: Iterator<Element>): Iterator<SearchResult> {

        override fun hasNext(): Boolean = results.hasNext()

        override fun next(): SearchResult = getResult(results.next())



        private fun getResult(row: Element): SearchResult {
            val titleLink = row.child(3).child(0).child(0)
            val tags = ObjectList<String>()
            tags.add(row.child(2).text())
            tags.add(row.child(4).text())
            tags.add(row.child(5).text().run { substring(0, length - 2) })
            tags.add("Сиды: " + row.child(6).text())
            tags.add("Личи: " + row.child(7).text())
            tags.add("Загрузок: " + row.child(8).text())
            tags.add("Добавлен: " + row.child(9).text())
            return EngineResult(titleLink.text(), titleLink.attr("href"), "", tags)
        }
    }

    private inner class EngineResult(override val title: String,
                                     titleLink: String,
                                     override val description: String,
                                     override val tags: List<String>): SearchResult {

        private val document: Document by lazy {
            newRequest()
                .url(forumUrl + titleLink)
                .execute()
                .parse()
        }



        override val data: Resource by lazy {
            TorrentURLResource(forumUrl + document
                .getElementsByClass("dl-stub dl-link dl-topic")[0].attr("href"))
        }

        override val image: Resource by lazy {
            val imageHolder = document.getElementsByClass("postImg")[0]
            AdvancedURLResource.newInstance(imageHolder
                .attr(if (imageHolder.tagName() == "var") "title" else "src"), getProxy(), cookies, userAgent)
        }
    }

    private inner class TorrentURLResource(url: String): AdvancedURLResource(url, getProxy(), newHeaders(cookies, userAgent)) {
        override fun getContentType(): String = "application/x-bittorrent"
    }
}