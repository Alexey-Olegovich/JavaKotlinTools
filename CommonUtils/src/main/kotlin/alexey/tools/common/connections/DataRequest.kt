package alexey.tools.common.connections

class DataRequest(val path: String,
                  val id: Int,
                  val type: Byte) {

    companion object {
        const val OPEN: Byte = 0
        const val NEXT: Byte = 1
        const val STOP: Byte = 2

        const val SIZE: Byte = 3
        const val TYPE: Byte = 4
        const val LIST: Byte = 5
    }
}