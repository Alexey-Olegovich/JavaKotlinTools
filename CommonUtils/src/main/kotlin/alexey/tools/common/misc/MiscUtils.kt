package alexey.tools.common.misc

import alexey.tools.common.events.EventBus
import alexey.tools.common.mods.ModLoader
import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.File
import javax.imageio.ImageIO

fun Iterable<Closeable>.close() = tryForEach { it.close() }

fun Array<out Closeable>.close() = tryForEach { it.close() }

fun EventBus.register(modLoader: ModLoader,
                      modsClassesName: String = "Mod",
                      initializeMethodName: String = "initialize") {

    val modEntries = modLoader.findObjects(modsClassesName, Any::class.java)
    for (modEntry in modEntries) silentTry {
        modEntry.javaClass
            .getDeclaredMethod(initializeMethodName, EventBus::class.java)
            .invoke(modEntry, this)
    }
}

fun cropImage(input: String, output: String,
              rows: Int, cols: Int,
              horizontalDelta: Int, verticalDelta: Int = horizontalDelta) {

    val inputImage = ImageIO.read(File(input))

    val partWidth = inputImage.width / cols
    val partHeight = inputImage.height / rows

    val newPartWidth = partWidth - horizontalDelta * 2
    val newPartHeight = partHeight - verticalDelta * 2

    val outputImage = BufferedImage(
        newPartWidth * cols,
        newPartHeight * rows,
        BufferedImage.TYPE_INT_ARGB)

    val g = outputImage.createGraphics()
    try {
        for (y in 0 ..< rows) for (x in 0 ..< cols) {
            val dx = x * newPartWidth
            val dy = y * newPartHeight

            val sx = x * partWidth + horizontalDelta
            val sy = y * partHeight + verticalDelta

            g.drawImage(inputImage,
                dx, dy, dx + newPartWidth, dy + newPartHeight,
                sx, sy, sx + newPartWidth, sy + newPartHeight, null)
        }
    } finally {
        g.dispose()
    }
    ImageIO.write(outputImage, output.getExtensionText(), File(output))
}