package alexey.tools.search.core

import alexey.tools.common.collections.ObjectCollection
import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.ObjectStorage
import alexey.tools.common.misc.newDaemonExecutor
import alexey.tools.common.misc.submitTask
import java.lang.IllegalStateException
import java.util.concurrent.*
import java.util.function.Consumer

class FairSearchController(private val engines: List<SearchEngine> = emptyList(),
                           private val executor: Executor = newDaemonExecutor()): SearchController {

    constructor(engines: List<SearchEngine>,
                threads: Int): this(engines, newDaemonExecutor(threads))



    override fun search(query: String): SearchController.SearchRequest =
        Request(engines, executor, query)



    class Request(private val engines: List<SearchEngine>,
                  private val executor: Executor,
                  private val query: String): SearchController.SearchRequest {

        private val searches = ObjectStorage<Iterator<SearchResult>>(engines.size)
        private var j = -1



        override fun getQuery(): String = query

        override fun next(amount: Int, listener: SearchController.Listener): Future<*> {
            if (amount < 1) throw IllegalStateException("amount < 1!")
            return executor.submitTask(Task(amount, listener))
        }

        override fun next(amount: Int): List<SearchResult> {
            if (amount < 1) return emptyList()
            synchronized(searches) {
                initialize()
                if (searches.isEmpty()) return emptyList()
                val results = ObjectList<SearchResult>(amount)
                var i = 0
                do {
                    val search = searches.get(j)
                    results.unsafeAdd(search.next())
                    if (search.hasNext()) {
                        j++
                    } else {
                        searches.justRemove(j)
                        if (searches.isEmpty()) break
                    }
                    if (j == searches.size) j = 0
                } while (++i < amount)
                return results
            }
        }

        override fun forEachRemaining(action: Consumer<in SearchResult>) {
            synchronized(searches) {
                initialize()
                if (searches.isEmpty()) return
                do {
                    if (j == searches.size) j = 0
                    val search = searches.get(j)
                    val result = search.next()
                    if (search.hasNext())
                        j++ else
                        searches.justRemove(j)
                    action.accept(result)
                } while (searches.isNotEmpty)
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

        override fun hasNext(): Boolean {
            synchronized(searches) {
                initialize()
                return searches.isNotEmpty
            }
        }



        private fun initialize() {
            if (j != -1) return
            val futures = ObjectCollection<Future<Iterator<SearchResult>>>(engines.size)
            engines.forEach { futures.unsafeAdd(executor.submitTask { it.search(query) }) }
            futures.forEach { it.get().let { i -> if (i.hasNext()) searches.unsafeAdd(i) } }
            j = 0
        }



        private inner class Task(private val amount: Int,
                                 private val listener: SearchController.Listener): Callable<Unit> {

            override fun call() {
                synchronized(searches) {
                    try {
                        initialize()
                        if (searches.isEmpty()) {
                            listener.onEnd(this@Request)
                            return
                        }
                        process()
                    } catch (e: Throwable) {
                        listener.onError(this@Request, e)
                    }
                }
            }



            private fun process() {
                var i = 0
                do {
                    val search = searches.get(j)
                    val result = search.next()
                    if (search.hasNext()) {
                        j++
                    } else {
                        searches.justRemove(j)
                        if (searches.isEmpty()) {
                            listener.onResult(this@Request, result)
                            listener.onEnd(this@Request)
                            return
                        }
                    }
                    if (j == searches.size) j = 0
                    if (listener.onResult(this@Request, result)) break
                } while (++i < amount)
                listener.onPart(this@Request)
            }
        }
    }



    companion object {
        fun newInstance(executor: Executor, vararg engines: SearchEngine) =
            FairSearchController(ObjectList.wrap(engines), executor)

        fun newInstance(threads: Int, vararg engines: SearchEngine) =
            FairSearchController(ObjectList.wrap(engines), newDaemonExecutor(threads))

        fun newInstance(vararg engines: SearchEngine) =
            FairSearchController(ObjectList.wrap(engines))
    }
}