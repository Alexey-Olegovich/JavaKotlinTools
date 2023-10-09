package alexey.tools.search.core

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.ObjectStorage
import alexey.tools.common.misc.newDaemonExecutor
import alexey.tools.common.misc.submitTask
import java.util.concurrent.Executor
import java.util.concurrent.Future

class FairSearchEngine(private val engines: List<SearchEngine> = emptyList(),
                       private val executor: Executor = newDaemonExecutor()): SearchEngine {

    override fun search(query: String): Iterator<SearchResult> = SearchResultIterator(query)



    private inner class SearchResultIterator(private val query: String): Iterator<SearchResult> {

        private val searches = ObjectStorage<Iterator<SearchResult>>(engines.size)
        private var j = -1



        override fun hasNext(): Boolean {
            synchronized(searches) {
                initialize()
                return searches.isNotEmpty
            }
        }

        override fun next(): SearchResult {
            synchronized(searches) {
                initialize()
                if (searches.isEmpty()) return DefaultSearchResult.EMPTY
                val search = searches.get(j)
                val result = search.next()
                if (search.hasNext()) {
                    j++
                } else {
                    searches.justRemove(j)
                    if (searches.isEmpty()) return result
                }
                if (j == searches.size) j = 0
                return result
            }
        }



        private fun initialize() {
            if (j != -1) return
            val futures = ObjectCollection<Future<Iterator<SearchResult>>>(engines.size)
            engines.forEach { futures.unsafeAdd(executor.submitTask { it.search(query) }) }
            futures.forEach { it.get().let { i -> if (i.hasNext()) searches.unsafeAdd(i) } }
            j = 0
        }
    }



    companion object {
        fun newInstance(vararg engines: SearchEngine): FairSearchEngine =
            FairSearchEngine(ObjectList.wrap(engines))

        fun newInstance(executor: Executor,
                        vararg engines: SearchEngine): FairSearchEngine =
            FairSearchEngine(ObjectList.wrap(engines), executor)
    }
}