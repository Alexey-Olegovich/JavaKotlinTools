package alexey.tools.hammer.objects

import alexey.tools.common.collections.ObjectList

class HammerSolid: HammerObject("solid") {

    val sides: List<HammerSide>
    val editor = HammerObject("editor").apply {
        //put("color", "0 0 0")
        put("visgroupshown", "1")
        put("visgroupautoshown", "1")
    }



    init {
        val sides = Array(6) { HammerSide() }
        this.sides = ObjectList.wrap(sides)

        for (i in 0..1) sides[i].apply {
            put("uaxis", "[1 0 0 0] 0.25")
            put("vaxis", "[0 -1 0 0] 0.25")
        }
        for (i in 2..3) sides[i].apply {
            put("uaxis", "[0 1 0 0] 0.25")
            put("vaxis", "[0 0 -1 0] 0.25")
        }
        for (i in 4..5) sides[i].apply {
            put("uaxis", "[1 0 0 0] 0.25")
            put("vaxis", "[0 0 -1 0] 0.25")
        }

        put(sides)
        put(editor)
    }



    fun set(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int,
            material: String = "tools/toolsnodraw") {

        sides[0].set(
            x1, y2, z2,
            x2, y2, z2,
            x2, y1, z2,
            material
        )
        sides[1].set(
            x1, y1, z1,
            x2, y1, z1,
            x2, y2, z1,
            material
        )

        sides[2].set(
            x1, y2, z2,
            x1, y1, z2,
            x1, y1, z1,
            material
        )
        sides[3].set(
            x2, y2, z1,
            x2, y1, z1,
            x2, y1, z2,
            material
        )

        sides[4].set(
            x2, y2, z2,
            x1, y2, z2,
            x1, y2, z1,
            material
        )
        sides[5].set(
            x2, y1, z1,
            x1, y1, z1,
            x1, y1, z2,
            material
        )
    }
}