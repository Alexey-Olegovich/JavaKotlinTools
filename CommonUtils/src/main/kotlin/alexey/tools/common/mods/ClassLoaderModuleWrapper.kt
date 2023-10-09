package alexey.tools.common.mods

import alexey.tools.common.resources.Resource

class ClassLoaderModuleWrapper(val module: Module, private val source: Resource = Resource.NULL): ClassLoaderModule {
    override fun isValid(): Boolean = module.isValid()
    override fun getObject(name: String): Any = module.getObject(name)
    override fun getSource(): Resource = source
}