package alexey.tools.common.resources

abstract class ResourceBase : Resource {

    override fun toString(): String = getResourceType() + ':' + getPath()

    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (this === other) return true
        if (javaClass !== other.javaClass) return false
        other as Resource
        return other.getResourceType() == getResourceType() && other.getPath() == getPath()
    }

    override fun hashCode(): Int = getResourceType().hashCode() * 31 + getPath().hashCode()
}