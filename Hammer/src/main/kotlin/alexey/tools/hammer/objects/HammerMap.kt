package alexey.tools.hammer.objects

import alexey.tools.hammer.core.IdGenerator
import alexey.tools.hammer.core.SequenceIdGenerator

class HammerMap(private val idGenerator: IdGenerator = SequenceIdGenerator()): HammerObjectBase() {

    val versionInfo = HammerObject("versioninfo").apply {
        put("editorversion", "400")
        put("editorbuild", "9573")
        put("mapversion", "11")
        put("formatversion", "100")
        put("prefab", "0")
    }

    val visGroups = HammerObject("visgroups")

    val viewSettings = HammerObject("viewsettings").apply {
        put("bSnapToGrid", "1")
        put("bShowGrid", "1")
        put("bShowLogicalGrid", "0")
        put("nGridSpacing", "16")
        put("bShow3DGrid", "0")
    }

    val world = HammerWorld(idGenerator)

    val cameras = HammerObject("cameras").apply {
        put("activecamera", "-1")
    }

    val cordon = HammerObject("cordon").apply {
        put("mins", "(-1024 -1024 -1024)")
        put("maxs", "(1024 1024 1024)")
        put("active", "0")
    }



    init {
        put(versionInfo)
        put(visGroups)
        put(viewSettings)
        put(world)
        put(cameras)
        put(cordon)
    }



    fun put(hammerEntity: HammerEntity) {
        hammerEntity.put("id", idGenerator.next().toString())
        super.put(hammerEntity)
    }
}