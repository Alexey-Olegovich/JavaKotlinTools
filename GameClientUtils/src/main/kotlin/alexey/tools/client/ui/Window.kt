package alexey.tools.client.ui

import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align

open class Window(title: String = "",
                  val style: WindowStyle = WindowStyle()): Table() {

    val titleLabel = Label(title, Label.LabelStyle(style.titleFont, style.titleFontColor))
    val header: Table = WindowTable()
    val body: Table = WindowTable()

    var isModal = true
    var isMovable = true
    var isResizable = false

    var resizeBorder = style.border
    var moveBorder = -1F

    private val input: WindowInput = WindowInput()



    init { initialize(title) }



    fun addHideButton(button: Button) {
        button.addListener(HideListener(this))
        header.add(button)
    }



    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (!isVisible) return null
        val hit = super.hit(x, y, touchable)
        return if (hit == null && isModal && (!touchable || getTouchable() == Touchable.enabled)) this else hit
    }

    override fun addActor(actor: Actor) {
        super.addActor(actor)
        addSounds(actor)
    }

    override fun row(): Cell<*> {
        return super.row().padTop(style.spacing)
    }



    private fun initialize(title: String) {
        pad(style.border)
        name = title
        touchable = Touchable.enabled
        clip = true
        background = style.stageBackground

        titleLabel.setEllipsis(true)
        body.background = style.background
        body.pad(style.border)
        header.add(titleLabel).expandX().fillX().left()

        add(header).expandX().fillX()
        row()
        add(body).expand().fill()

        moveBorder = padTop + header.minHeight
        addListener(input)
        super.setVisible(false)
    }

    private fun addSounds(target: Actor) {
        when (target) {
            is Button -> {
                style.clickSound?.let { target.addClickSound(it) }
                style.hoverSound?.let { target.addHoverSound(it) }
            }
            is Group -> for (child in target.children) addSounds(child)
        }
    }



    private inner class WindowTable: Table() {

        override fun addActor(actor: Actor) {
            super.addActor(actor)
            addSounds(actor)
        }

        override fun row(): Cell<*> {
            return super.row().padTop(style.spacing)
        }
    }

    private inner class WindowInput: InputListener() {

        private var edge = 0

        private var startX = 0f
        private var startY = 0f
        private var lastX = 0f
        private var lastY = 0f



        private fun updateEdge(x: Float, y: Float) {
            val width = width
            val height = height
            edge = 0
            if (isResizable) {
                if (x < resizeBorder && x > 0F) edge = Align.left
                if (x > width - resizeBorder && x < width) edge = edge or Align.right
                if (y < resizeBorder && y > 0F) edge = edge or Align.bottom
                if (y > height - resizeBorder && y < height) edge = edge or Align.top
            }
            if (isMovable && edge == 0 && y > height - moveBorder && y < height && x > 0F && x < width) edge = MOVE
        }



        override fun touchDragged(event: InputEvent, tx: Float, ty: Float, pointer: Int) {
            if (edge == 0) return

            var width = width
            var height = height

            var windowX = x
            var windowY = y

            val minWidth = minWidth
            val minHeight = minHeight

            val stage = stage

            if (edge and MOVE != 0) {
                windowX += tx - startX
                windowY += ty - startY
                when {
                    windowX < 0F                  -> windowX = 0F
                    windowX + width > stage.width -> windowX = stage.width - width
                }
                when {
                    windowY < 0F                    -> windowY = 0F
                    windowY + height > stage.height -> windowY = stage.height - height
                }
            }

            if (edge and Align.left != 0) {
                var amountX = tx - startX
                when {
                    width - amountX < minWidth -> amountX = -(minWidth - width)
                    windowX + amountX < 0      -> amountX = -windowX
                }
                width -= amountX
                windowX += amountX
            }

            if (edge and Align.bottom != 0) {
                var amountY = ty - startY
                when {
                    height - amountY < minHeight -> amountY = -(minHeight - height)
                    windowY + amountY < 0        -> amountY = -windowY
                }
                height -= amountY
                windowY += amountY
            }

            if (edge and Align.right != 0) {
                var amountX = tx - lastX - width
                when {
                    width + amountX < minWidth              -> amountX = minWidth - width
                    windowX + width + amountX > stage.width -> amountX = stage.width - windowX - width
                }
                width += amountX
            }

            if (edge and Align.top != 0) {
                var amountY = ty - lastY - height
                when {
                    height + amountY < minHeight              -> amountY = minHeight - height
                    windowY + height + amountY > stage.height -> amountY = stage.height - windowY - height
                }
                height += amountY
            }

            setBounds(windowX, windowY, width, height)
        }

        override fun touchDown(event: InputEvent, tx: Float, ty: Float, pointer: Int, button: Int): Boolean {
            if (button == 0) {
                updateEdge(tx, ty)
                startX = tx
                startY = ty
                lastX = tx - width
                lastY = ty - height
            }
            return edge != 0 || isModal
        }

        override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
            edge = 0
        }

        override fun keyDown(event: InputEvent, keycode: Int): Boolean {
            return isModal
        }

        override fun mouseMoved(event: InputEvent, x: Float, y: Float): Boolean {
            return isModal
        }

        override fun scrolled(event: InputEvent, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
            return isModal
        }

        override fun keyUp(event: InputEvent, keycode: Int): Boolean {
            return isModal
        }

        override fun keyTyped(event: InputEvent, character: Char): Boolean {
            return isModal
        }
    }



    companion object {
        private const val MOVE = 1 shl 5
    }
}