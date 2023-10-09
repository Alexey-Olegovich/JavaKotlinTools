package alexey.tools.hammer.objects

import alexey.tools.hammer.core.IdGenerator
import alexey.tools.hammer.core.SequenceIdGenerator

class HammerWorld(private val generator: IdGenerator = SequenceIdGenerator()): HammerObject("world") {

    init {
        put("id", generator.next().toString())
        put("mapversion", "11")
        put("classname", "worldspawn")
        put("detailmaterial", "detail/detailsprites")
        put("detailvbsp", "detail.vbsp")
        put("maxpropscreenwidth", "-1")
        put("skyname", "sky_day01_05")
    }



    fun put(solid: HammerSolid) {
        solid.put("id", generator.next().toString())
        solid.sides.forEach { it.put("id", generator.next().toString()) }
        super.put(solid)
    }
}