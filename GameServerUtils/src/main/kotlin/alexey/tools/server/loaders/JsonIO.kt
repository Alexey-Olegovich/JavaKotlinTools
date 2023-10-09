package alexey.tools.server.loaders

import alexey.tools.server.misc.*
import alexey.tools.common.loaders.ObjectIO
import alexey.tools.common.collections.convert
import alexey.tools.common.mods.ModLoader
import alexey.tools.common.resources.Resource
import com.fasterxml.jackson.databind.ObjectMapper

class JsonIO(val modLoader: ModLoader,
             val objectMapper: ObjectMapper = ObjectMapper()): ObjectIO {



    override fun <T> readObject(path: String, type: Class<T>): T? {
        return objectMapper.readValue(modLoader.findResource(path) ?: return null, type)
    }

    override fun <T> readObjects(path: String, type: Class<T>): List<T> =
        modLoader.findResources(path).convert { objectMapper.readValue(it, type) }

    override fun writeObject(path: String, obj: Any?): Resource? {
        return (modLoader.findResourceToWrite(path) ?: return null).also { objectMapper.writeValue(it, obj) }
    }

    override fun getExtension(): String = ".json"

    override fun getContentType(): String = "application/json"
}