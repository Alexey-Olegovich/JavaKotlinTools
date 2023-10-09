package alexey.tools.search.core

import alexey.tools.common.resources.Resource

interface SearchResult {
    val title: String
    val image: Resource
    val data: Resource
    val description: String
    val tags: List<String>
}