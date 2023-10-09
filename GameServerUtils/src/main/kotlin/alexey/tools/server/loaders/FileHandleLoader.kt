package alexey.tools.server.loaders

import alexey.tools.common.loaders.ResourceLoader
import alexey.tools.common.mods.ModLoader
import alexey.tools.common.resources.Resource
import alexey.tools.server.misc.ResourceFileHandle
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

abstract class FileHandleLoader <T> (contentFinder: ModLoader): ResourceLoader<T>(contentFinder) {
    override fun apply(resource: Resource): T = apply(when (resource.getResourceType()) {
        "asset" -> Gdx.files.internal(resource.getPath())
        "file" -> Gdx.files.absolute(resource.getPath())
        else -> ResourceFileHandle(resource)
    })
    abstract fun apply(fileHandle: FileHandle): T
}