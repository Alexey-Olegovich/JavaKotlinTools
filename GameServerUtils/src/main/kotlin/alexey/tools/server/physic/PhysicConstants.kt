package alexey.tools.server.physic

object PhysicConstants {
    val DYNAMIC = Pair("dynamic", false)
    val FRICTION = Pair("friction", 0.2F)
    val GROUP = Pair<String, Short>("group", 0)
    val DENSITY = Pair("density", 0.1F)
    val RESTITUTION = Pair("restitution", 0F)
    val CATEGORY = Pair<String, Short>("category", 0x0001)
    val MASK = Pair<String, Short>("mask", -1)
    val SENSOR = Pair("sensor", false)
    val LINEAR_DAMPING = Pair("linearDamping", 0F)
    val ANGULAR_DAMPING = Pair("angularDamping", 0F)
    val BULLET = Pair("bullet", false)
    val FIXED_ROTATION = Pair("fixedRotation", false)
}