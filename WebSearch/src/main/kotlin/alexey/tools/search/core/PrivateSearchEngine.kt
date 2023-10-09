package alexey.tools.search.core

interface PrivateSearchEngine: SearchEngine {
    fun login(user: String, password: String)
}