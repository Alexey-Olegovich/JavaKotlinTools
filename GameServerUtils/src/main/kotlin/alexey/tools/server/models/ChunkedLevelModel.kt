package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.common.math.ImmutableIntVector2

interface ChunkedLevelModel {
    val properties: ImmutableVariables get() = ImmutableVariables.DEFAULT
    val chunkSize: Float get() = 0F
    fun get(position: ImmutableIntVector2): List<EntityModel> = emptyList()
    fun asMap(): Map<ImmutableIntVector2, List<EntityModel>> = emptyMap()

    companion object {
        val DEFAULT = object : ChunkedLevelModel {}
    }
}