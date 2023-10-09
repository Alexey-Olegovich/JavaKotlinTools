package alexey.tools.server.models

import alexey.tools.common.collections.ImmutableIntList
import alexey.tools.server.serializers.EntityIdChunkDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

class TiledChunk {

    val height = 0
    val width = 0
    val x = 0
    val y = 0

    @JsonDeserialize(using = EntityIdChunkDeserializer::class)
    val data: ImmutableIntList = ImmutableIntList.EMPTY
}