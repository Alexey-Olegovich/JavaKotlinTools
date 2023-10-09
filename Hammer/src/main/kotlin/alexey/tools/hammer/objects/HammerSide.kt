package alexey.tools.hammer.objects

class HammerSide: HammerObject("side") {

    init {
        put("rotation", "0")
        put("lightmapscale", "16")
        put("smoothing_groups", "0")
    }



    fun set(x1: Int, y1: Int, z1: Int,
            x2: Int, y2: Int, z2: Int,
            x3: Int, y3: Int, z3: Int, material: String) {

        put("plane", buildString {
            append('(')
            append(x1)
            append(' ')
            append(y1)
            append(' ')
            append(z1)
            append(") (")
            append(x2)
            append(' ')
            append(y2)
            append(' ')
            append(z2)
            append(") (")
            append(x3)
            append(' ')
            append(y3)
            append(' ')
            append(z3)
            append(')')
        })
        put("material", material)
    }
}