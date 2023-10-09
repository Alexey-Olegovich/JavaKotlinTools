package alexey.tools.server.world

import alexey.tools.common.level.ChunkManager
import alexey.tools.common.level.ContactProcessor
import alexey.tools.common.level.StateManager
import alexey.tools.common.misc.hasAnyMethod
import alexey.tools.common.misc.hasMethod

open class AdvancedMasterSystem<T>: MasterSystem(),
    ChunkManager.Listener,
    StateManager.Listener,
    ContactProcessor<T> {



    fun hasChunkManagerListener() = hasChunkManagerListener(javaClass)

    fun hasStateManagerListener() = hasStateManagerListener(javaClass)

    fun hasContactProcessor() = hasContactProcessor(javaClass)



    companion object {

        private val ADVANCED_MASTER_SYSTEM_CLASS = AdvancedMasterSystem::class.java
        private val CONTACT_PROCESSOR_METHOD = ContactProcessor::class.java.declaredMethods[0]
        private val CHUNK_MANAGER_LISTENER_METHODS = ChunkManager.Listener::class.java.declaredMethods
        private val STATE_MANAGER_LISTENER_METHODS = StateManager.Listener::class.java.declaredMethods



        fun hasChunkManagerListener(type: Class<out MasterSystem>) =
            type.hasAnyMethod(CHUNK_MANAGER_LISTENER_METHODS, ADVANCED_MASTER_SYSTEM_CLASS)

        fun hasStateManagerListener(type: Class<out MasterSystem>) =
            type.hasAnyMethod(STATE_MANAGER_LISTENER_METHODS, ADVANCED_MASTER_SYSTEM_CLASS)

        fun hasContactProcessor(type: Class<out AdvancedMasterSystem<*>>) =
            type.hasMethod(CONTACT_PROCESSOR_METHOD, ADVANCED_MASTER_SYSTEM_CLASS)
    }
}