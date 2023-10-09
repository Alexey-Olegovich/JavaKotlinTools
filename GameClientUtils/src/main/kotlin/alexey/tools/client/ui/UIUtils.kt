package alexey.tools.client.ui

import alexey.tools.common.loaders.ObjectIO
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

fun <T: Actor> Cell<T>.scale(amount: Float): Cell<T> =
    size(actor.width * amount, actor.height * amount)

fun <T: Actor> Cell<T>.resizeWidth(amount: Float): Cell<T> =
    size(amount, amount * actor.height / actor.width)

fun <T: Actor> Cell<T>.resizeHeight(amount: Float): Cell<T> =
    size(amount * actor.width / actor.height, amount)

fun Container<*>.resizeActorWidth(amount: Float) {
    size(amount, amount * actor.height / actor.width)
}

fun Actor.resizeWidth(amount: Float) {
    setSize(amount, amount * height / width)
}

fun Actor.resizeHeight(amount: Float) {
    setSize(amount * width / height, amount)
}

fun Actor.resizeMax() {
    if (stage.width > stage.height)
        resizeWidth(stage.width) else
        resizeHeight(stage.height)
}

fun Actor.resizeFill() {
    val aRatio = width / height
    val sRatio = stage.width / stage.height
    if (sRatio > aRatio)
        setSize(stage.width, stage.width / aRatio) else
        setSize(stage.height * aRatio, stage.height)
}

fun Actor.centerX() {
    x = stage.width / 2F - width / 2F
}

fun Actor.centerY() {
    y = stage.height / 2F - height / 2F
}

fun Actor.moveToCenter() {
    centerX()
    centerY()
}

fun Table.wrapContent() {
    width = minWidth
    height = minHeight
}

fun Actor.hide() {
    isVisible = false
}

fun Actor.show() {
    isVisible = true
}

fun Button.addClickSound(sound: Sound) {
    addListener(ClickSound(sound))
}

fun Button.addHoverSound(sound: Sound) {
    addListener(HoverSound(sound))
}

fun ObjectIO.newFancyTextButton(text: String, style: String = ""): TextButton {
    val buttonStyle = obtainObject(style, FancyTextButtonStyle::class.java)
    val button = TextButton(text, buttonStyle)
    buttonStyle.clickSound?.let { button.addClickSound(it) }
    buttonStyle.hoverSound?.let { button.addHoverSound(it) }
    return button
}

fun ObjectIO.obtainLabelStyle(style: String = ""): LabelStyle =
    obtainObject(style, LabelStyle::class.java)

fun ObjectIO.newTextButton(text: String = "", style: String = ""): TextButton =
    TextButton(text, obtainObject(style, TextButton.TextButtonStyle::class.java))

fun ObjectIO.newLabel(text: String = "", style: String = ""): Label =
    Label(text, obtainLabelStyle(style))

fun ObjectIO.newImageButton(style: String = ""): ImageButton =
    ImageButton(obtainObject(style, ImageButton.ImageButtonStyle::class.java))

fun ObjectIO.newTextField(text: String = "", style: String = ""): TextField =
    TextField(text, obtainObject(style, TextField.TextFieldStyle::class.java))

fun ObjectIO.newCheckBox(text: String = "", style: String = ""): CheckBox =
    CheckBox(text, obtainObject(style, CheckBoxStyle::class.java))

fun ObjectIO.newSlider(min: Float = 0F, max: Float = 1F, step: Float = 0.1F,
                       vertical: Boolean = false, style: String = ""): Slider =
    Slider(min, max, step, vertical, obtainObject(style, SliderStyle::class.java))

inline fun Actor.addChangeListener(crossinline action: () -> Unit) =
    addListener(object : ChangeListener() {
        override fun changed(event: ChangeEvent, actor: Actor) { action() }
    })