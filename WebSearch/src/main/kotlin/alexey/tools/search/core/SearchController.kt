package alexey.tools.search.core

import alexey.tools.common.concurrent.AbstractRunnableFuture
import java.util.concurrent.Future

interface SearchController: SearchEngine {

    override fun search(query: String): SearchRequest

    interface Listener {
        fun onResult(searchRequest: SearchRequest, searchResult: SearchResult): Boolean = false
        fun onPart(searchRequest: SearchRequest) {}
        fun onError(searchRequest: SearchRequest, error: Throwable) {}
        fun onEnd(searchRequest: SearchRequest) {}

        companion object {
            val DEFAULT = object : Listener {}
        }
    }

    interface SearchRequest: Iterator<SearchResult> {
        fun getQuery(): String = ""
        fun next(amount: Int, listener: Listener): Future<*> = AbstractRunnableFuture.INSTANCE
        fun next(amount: Int): List<SearchResult> = emptyList()
        override fun next(): SearchResult = DefaultSearchResult.EMPTY
        override fun hasNext(): Boolean = false

        companion object {
            val EMPTY = object : SearchRequest {}
        }
    }
}