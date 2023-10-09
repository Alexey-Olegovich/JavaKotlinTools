package alexey.tools.server.misc

import alexey.tools.common.misc.tryForEach
import com.badlogic.gdx.utils.Disposable

fun Iterable<Disposable>.dispose() = tryForEach { it.dispose() }

fun Array<out Disposable>.dispose() = tryForEach { it.dispose() }