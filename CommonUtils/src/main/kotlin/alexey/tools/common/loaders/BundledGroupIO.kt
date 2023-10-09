package alexey.tools.common.loaders

class BundledGroupIO(private val objectIO: ObjectIO): ObjectIO {

    private val data = HashMap<String, BundledTypeGroup>()



    fun obtain(root: String): BundledTypeGroup = data.getOrPut(root) { BundledTypeGroup(objectIO, root) }



    override fun <T> readObject(path: String, type: Class<T>): T? =
        data[path]?.readObject(type)

    override fun getExtension(): String = objectIO.getExtension()

    override fun getContentType(): String = objectIO.getContentType()
}