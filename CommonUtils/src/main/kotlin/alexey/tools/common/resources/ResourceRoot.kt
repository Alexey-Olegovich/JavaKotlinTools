package alexey.tools.common.resources

import java.io.Closeable

interface ResourceRoot: Resource, Closeable {

    override fun close() {}

    companion object {
        val NULL: ResourceRoot = object : ResourceRoot, ResourceBase() {
            override fun canRead(): Boolean = false
            override fun canWrite(): Boolean = false
            override fun getResourceType(): String = "null"
            override fun toString(): String = "null:"
            override fun isValid(): Boolean = false
        }
    }
}