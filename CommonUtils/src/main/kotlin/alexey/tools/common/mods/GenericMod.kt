package alexey.tools.common.mods

import alexey.tools.common.resources.Resource

interface GenericMod: Mod {
    override fun getLocalObject(name: String): Any = getModule().getObject(name)
    override fun getLocalResource(path: String): Resource = getResource().getResource(path)
}