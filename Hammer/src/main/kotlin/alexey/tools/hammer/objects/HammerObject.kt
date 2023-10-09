package alexey.tools.hammer.objects

import java.io.Writer

open class HammerObject(val name: String = ""): HammerObjectBase() {

    val properties = HashMap<String, String>()



    fun put(key: String, value: String) {
        properties[key] = value
    }



    override fun writeTo(writer: Writer, pads: Int) {
        writer.write(name, pads)
        writer.write('\n'.code)
        writer.write("{\n", pads)
        val times = pads + 1
        properties.forEach { (k, v) ->
            writer.write('\"', times)
            writer.write(k)
            writer.write("\" \"")
            writer.write(v)
            writer.write("\"\n")
        }
        super.writeTo(writer, times)
        writer.write("}\n", pads)
    }



    private fun Writer.write(text: String, times: Int) {
        for (i in 0 until times) write('\t'.code)
        write(text)
    }

    private fun Writer.write(symbol: Char, times: Int) {
        for (i in 0 until times) write('\t'.code)
        write(symbol.code)
    }
}