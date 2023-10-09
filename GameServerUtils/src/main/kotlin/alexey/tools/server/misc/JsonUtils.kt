package alexey.tools.server.misc

import alexey.tools.common.context.Variables
import alexey.tools.common.misc.tryClose
import alexey.tools.common.resources.Resource
import alexey.tools.server.serializers.VariablesDeserializer
import alexey.tools.server.serializers.VariablesSerializer
import alexey.tools.server.loaders.JsonIO
import com.fasterxml.jackson.annotation.JsonIgnoreType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule

@JsonIgnoreType
private class IgnoreType

fun <T> ObjectMapper.readValue(source: Resource, outputClass: Class<T>): T =
    source.getInputStream().tryClose { readValue(this, outputClass) }

fun ObjectMapper.writeValue(source: Resource, obj: Any?) =
    source.getOutputStream().tryClose { writerWithDefaultPrettyPrinter().writeValue(this, obj) }

fun ObjectMapper.registerVariablesModule() {
    val module = SimpleModule(Variables::class.java.name)
    module.addSerializer(Variables::class.java, VariablesSerializer())
    module.addDeserializer(Variables::class.java, VariablesDeserializer())
    registerModule(module)
}

fun ObjectMapper.ignoreClass(type: Class<*>) {
    addMixIn(type, IgnoreType::class.java)
}

fun JsonIO.obtainVariables(path: String): Variables {
    objectMapper.registerVariablesModule()
    return readObject(path, Variables::class.java) ?: Variables()
}