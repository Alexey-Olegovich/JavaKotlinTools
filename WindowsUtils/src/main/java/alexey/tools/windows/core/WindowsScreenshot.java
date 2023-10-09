package alexey.tools.windows.core;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import org.jetbrains.annotations.NotNull;

import java.awt.image.*;

public class WindowsScreenshot {

    public static WinDef.HWND getDesktopWindow() {
        return User32.INSTANCE.GetDesktopWindow();
    }

    @NotNull
    public static WinDef.RECT getRectangle(WinDef.HWND target) {
        final WinDef.RECT rect = new WinDef.RECT();
        if (!User32.INSTANCE.GetWindowRect(target, rect))
            throw new Win32Exception(Native.getLastError());
        return rect;
    }

    @NotNull
    public static WinDef.RECT getRectangle(int x, int y, int width, int height) {
        final WinDef.RECT rect = new WinDef.RECT();
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;
        return rect;
    }

    public static BufferedImage getScreenshot(final WinDef.HWND target, final int x, final int y, final int width, final int height) throws RuntimeException {
        return getScreenshot(target, getRectangle(x, y, width, height));
    }

    public static BufferedImage getScreenshot(final WinDef.HWND target) throws RuntimeException {
        return getScreenshot(target, getRectangle(target));
    }

    public static BufferedImage getScreenshot() throws RuntimeException {
        return getScreenshot(getDesktopWindow());
    }

    public static BufferedImage getScreenshot(final WinDef.HWND target, @NotNull final WinDef.RECT rect) throws RuntimeException {
        final int windowWidth = rect.right - rect.left;
        final int windowHeight = rect.bottom - rect.top;

        final WinDef.HDC hdcTarget = User32.INSTANCE.GetDC(target);
        if (hdcTarget == null) {
            throw new Win32Exception(Native.getLastError());
        }

        WinDef.HDC hdcTargetMem = null;
        WinDef.HBITMAP hBitmap = null;
        WinNT.HANDLE hOriginal = null;
        BufferedImage image = null;
        RuntimeException createException = null;
        RuntimeException freeException = null;

        try {

            hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);
            if (hdcTargetMem == null) throw new Win32Exception(Native.getLastError());

            hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, windowWidth, windowHeight);
            if (hBitmap == null) throw new Win32Exception(Native.getLastError());

            hOriginal = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);
            if (hOriginal == null) throw new Win32Exception(Native.getLastError());

            if (!GDI32.INSTANCE.BitBlt(hdcTargetMem, 0, 0,
                    windowWidth, windowHeight, hdcTarget, rect.left, rect.top,
                    GDI32.SRCCOPY)) throw new Win32Exception(Native.getLastError());

            WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
            bmi.bmiHeader.biWidth = windowWidth;
            bmi.bmiHeader.biHeight = -windowHeight;
            bmi.bmiHeader.biPlanes = 1;
            bmi.bmiHeader.biBitCount = 32;
            bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

            final Memory buffer = new Memory((long) windowWidth * windowHeight * 4);
            final int resultOfDrawing = GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, windowHeight, buffer, bmi,
                    WinGDI.DIB_RGB_COLORS);
            if (resultOfDrawing == 0 || resultOfDrawing == WinError.ERROR_INVALID_PARAMETER) {
                throw new Win32Exception(Native.getLastError());
            }

            final int bufferSize = windowWidth * windowHeight;
            final DataBuffer dataBuffer = new DataBufferInt(buffer.getIntArray(0, bufferSize), bufferSize);
            final WritableRaster raster = Raster.createPackedRaster(dataBuffer, windowWidth, windowHeight, windowWidth,
                    SCREENSHOT_BAND_MASKS, null);
            image = new BufferedImage(SCREENSHOT_COLOR_MODEL, raster, false, null);

        } catch (RuntimeException e) {
            createException = e;
        }

        if (hOriginal != null) {
            final WinNT.HANDLE result = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);
            if (result == null || WinGDI.HGDI_ERROR.equals(result))
                freeException = new Win32Exception(Native.getLastError());
        }

        if (hBitmap != null)
            if (!GDI32.INSTANCE.DeleteObject(hBitmap))
                freeException = new Win32Exception(Native.getLastError());

        if (hdcTargetMem != null)
            if (!GDI32.INSTANCE.DeleteDC(hdcTargetMem))
                freeException = new Win32Exception(Native.getLastError());

        if (0 == User32.INSTANCE.ReleaseDC(target, hdcTarget))
            freeException = new IllegalStateException("Device context did not release properly.");

        if (createException != null) throw createException;
        if (freeException != null) throw freeException;

        return image;
    }



    private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(24, 0x00FF0000, 0xFF00, 0xFF);

    private static final int[] SCREENSHOT_BAND_MASKS = {
            SCREENSHOT_COLOR_MODEL.getRedMask(),
            SCREENSHOT_COLOR_MODEL.getGreenMask(),
            SCREENSHOT_COLOR_MODEL.getBlueMask()
    };
}
