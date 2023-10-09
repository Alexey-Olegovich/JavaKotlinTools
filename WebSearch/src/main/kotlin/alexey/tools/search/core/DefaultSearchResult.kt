package alexey.tools.search.core

import alexey.tools.common.resources.Resource

class DefaultSearchResult(override val title: String = "",
                          override val data: Resource = Resource.NULL,
                          override val image: Resource = Resource.NULL,
                          override val description: String = "",
                          override val tags: List<String> = emptyList()): SearchResult {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchResult

        if (title != other.title) return false
        if (data != other.data) return false
        if (image != other.image) return false
        if (description != other.description) return false
        return tags == other.tags
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + tags.hashCode()
        return result
    }

    override fun toString(): String {
        return "DefaultSearchResult(\ntitle = '$title',\ndata = '$data',\nimage = '$image',\ndescription = '$description',\ntags = $tags)"
    }



    companion object {
        val EMPTY = DefaultSearchResult()
    }
}