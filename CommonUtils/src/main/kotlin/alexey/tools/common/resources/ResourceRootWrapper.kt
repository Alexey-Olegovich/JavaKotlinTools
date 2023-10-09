package alexey.tools.common.resources

import java.io.InputStream
import java.io.OutputStream

open class ResourceRootWrapper(private val resource: Resource): ResourceBase(), ResourceRoot {
    override fun getResource(relativePath: String): Resource = resource.getResource(relativePath)

    override fun getOutputStream(): OutputStream = resource.getOutputStream()
    override fun getInputStream(): InputStream = resource.getInputStream()

    override fun canRead(): Boolean = resource.canRead()
    override fun canWrite(): Boolean = resource.canWrite()
    override fun isValid(): Boolean = resource.isValid()
    override fun length(): Long = resource.length()

    override fun getPath(): String = resource.getPath()
    override fun getResourceType(): String = resource.getResourceType()
    override fun getContentType(): String = resource.getContentType()
}