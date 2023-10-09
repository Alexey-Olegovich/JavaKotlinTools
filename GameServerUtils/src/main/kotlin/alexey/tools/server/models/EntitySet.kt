package alexey.tools.server.models

class EntitySet(val entities: List<EntityModel> = emptyList(),
                val id: Int = -1) {

    companion object {
        val DEFAULT = EntitySet()
    }
}