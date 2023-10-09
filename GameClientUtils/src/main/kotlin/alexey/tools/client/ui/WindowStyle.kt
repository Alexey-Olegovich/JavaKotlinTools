package alexey.tools.client.ui

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.ui.Window

class WindowStyle: Window.WindowStyle() {
    var border: Float = 0F
    var spacing: Float = 0F
    var clickSound: Sound? = null
    var hoverSound: Sound? = null
}