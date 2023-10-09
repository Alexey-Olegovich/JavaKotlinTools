package alexey.tools.client.ui

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle

class FancyTextButtonStyle: TextButtonStyle() {
    var clickSound: Sound? = null
    var hoverSound: Sound? = null
}