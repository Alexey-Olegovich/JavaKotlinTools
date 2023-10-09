package alexey.tools.client.viewport

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport

class FairViewport(private val maxView: Float = 100F, camera: Camera = OrthographicCamera()): Viewport() {

    init { this.camera = camera }

    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        val worldRatio = screenHeight / screenWidth.toFloat()
        if (screenHeight > screenWidth)
            setWorldSize(maxView / worldRatio, maxView) else
            setWorldSize(maxView, maxView * worldRatio)
        setScreenBounds(0, 0, screenWidth, screenHeight)
        apply(centerCamera)
    }
}