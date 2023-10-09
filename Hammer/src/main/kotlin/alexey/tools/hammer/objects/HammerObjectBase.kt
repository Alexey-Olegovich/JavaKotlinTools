package alexey.tools.hammer.objects

import alexey.tools.common.collections.ObjectList
import java.io.Writer

open class HammerObjectBase {

    private val objects = ObjectList<HammerObjectBase>()



    fun put(obj: HammerObjectBase) {
        objects.add(obj)
    }

    fun put(obj: Array<out HammerObjectBase>) {
        objects.addAll(obj)
    }

    fun put(obj: Collection<HammerObjectBase>) {
        objects.addAll(obj)
    }



    fun writeTo(writer: Writer) {
        writeTo(writer, 0)
    }

    protected open fun writeTo(writer: Writer, pads: Int) {
        objects.forEach { it.writeTo(writer, pads) }
    }
}