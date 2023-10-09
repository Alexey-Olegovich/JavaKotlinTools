package alexey.tools.common.mods

import alexey.tools.common.resources.Resource

interface ClassLoaderModule: Module {

    override fun getObject(name: String): Any = getLocalType(name).getDeclaredConstructor().newInstance()

    fun getSource(): Resource = Resource.NULL
    fun getLocalType(name: String): Class<*> = throw UnsupportedOperationException("getType")
    fun getLocalResource(name: String): Resource? = getSource().getResource(name).run { if (canRead()) this else null }
    fun getGlobalType(name: String): Class<*> = getLocalType(name)
    fun getGlobalResource(name: String): Resource? = getLocalResource(name)

    companion object {
        val NULL = object : ClassLoaderModule {}
    }
}