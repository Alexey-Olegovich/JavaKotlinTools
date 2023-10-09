package alexey.tools.client.viewport

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport

class UnfairViewport(private val minView: Float = 100F, camera: Camera = OrthographicCamera()): Viewport() {

    init { this.camera = camera }

    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        val ratio = screenWidth.toFloat() / screenHeight
        if (screenWidth > screenHeight)
            setWorldSize(minView * ratio, minView) else
            setWorldSize(minView, minView / ratio)
        setScreenBounds(0, 0, screenWidth, screenHeight)
        apply(centerCamera)
    }
}