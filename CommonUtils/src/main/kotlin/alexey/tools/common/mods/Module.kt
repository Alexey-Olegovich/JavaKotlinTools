package alexey.tools.common.mods

interface Module {
    fun getObject(name: String): Any = throw UnsupportedOperationException("getObject")
    fun isValid() = true

    companion object {
        val NULL = object : Module {
            override fun isValid(): Boolean = false
        }
    }
}