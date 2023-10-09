package alexey.tools.common.mods

import alexey.tools.common.resources.Resource

interface Mod {
    fun getId(): String = ""

    fun getModule(): Module = Module.NULL
    fun getResource(): Resource = Resource.NULL

    fun getLocalObject(name: String) = getModule().getObject("${getId()}.$name")
    fun getLocalResource(path: String) = getResource().getResource("${getId()}/$path")

    fun isValid(): Boolean = true
}