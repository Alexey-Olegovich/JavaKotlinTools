package alexey.tools.windows.core

import com.sun.jna.platform.win32.BaseTSD
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import java.awt.Desktop
import java.io.File

object WindowsRobot {

    private val keyboard = WinUser.INPUT()
    private val mouse = WinUser.INPUT()

    private val amount = WinDef.DWORD(1L)
    private val cursorPosition = WinDef.POINT(0, 0)



    init {
        keyboard.type = WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD.toLong())
        keyboard.input.setType("ki")
        keyboard.input.ki.apply {
            wVk = WinDef.WORD(0L)
            dwFlags = WinDef.DWORD(0L)
            wScan = WinDef.WORD(0L)
            time = WinDef.DWORD(0L)
            dwExtraInfo = BaseTSD.ULONG_PTR(0L)
        }

        mouse.type = WinDef.DWORD(WinUser.INPUT.INPUT_MOUSE.toLong())
        mouse.input.setType("mi")
        mouse.input.mi.apply {
            dwFlags = WinDef.DWORD(0L)
            time = WinDef.DWORD(0L)
            mouseData = WinDef.DWORD(0L)
            dwExtraInfo = BaseTSD.ULONG_PTR(0L)
        }
    }



    fun setCursorPos(x: Long, y: Long) {
        User32.INSTANCE.SetCursorPos(x, y)
    }

    fun getCursorPos(): WinDef.POINT {
        User32.INSTANCE.GetCursorPos(cursorPosition)
        return cursorPosition
    }

    fun mouseWheel(delta: Long) {
        mouse.input.mi.mouseData.setValue(delta)
        mouseAction(2048L)
    }

    fun keyPress(keycode: Long) = keyboardAction(keycode, 0L)

    fun keyRelease(keycode: Long) = keyboardAction(keycode, 2L)

    fun mouseLeftPress() = mouseAction(2L)

    fun mouseLeftRelease() = mouseAction(4L)

    fun mouseRightPress() = mouseAction(8L)

    fun mouseRightRelease() = mouseAction(16L)

    fun mouseMiddlePress() = mouseAction(32L)

    fun mouseMiddleRelease() = mouseAction(64L)

    fun openFile(path: String) = Desktop.getDesktop().open(File(path))



    @Suppress("UNCHECKED_CAST")
    private fun sendInput(device: WinUser.INPUT) {
        User32.INSTANCE.SendInput(amount, device.toArray(1) as Array<WinUser.INPUT>, device.size())
    }

    private fun keyboardAction(keycode: Long, action: Long) {
        keyboard.input.ki.apply {
            wVk.setValue(keycode)
            dwFlags.setValue(action)
        }
        sendInput(keyboard)
    }

    private fun mouseAction(action: Long) {
        mouse.input.mi.dwFlags.setValue(action)
        sendInput(mouse)
    }
}