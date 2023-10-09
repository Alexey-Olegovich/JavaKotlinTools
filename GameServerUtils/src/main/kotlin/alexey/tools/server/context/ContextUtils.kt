package alexey.tools.server.context

import alexey.tools.server.loaders.BinaryIO
import alexey.tools.common.context.*
import alexey.tools.common.loaders.ObjectIO
import alexey.tools.common.mods.ModLoader
import alexey.tools.server.core.Game
import alexey.tools.server.loaders.JsonIO
import alexey.tools.server.misc.obtainVariables
import alexey.tools.server.misc.registerVariablesModule
import com.badlogic.gdx.utils.Disposable

fun RegistrableContext.registerGame(game: Game<*>) =
    register(game, Game::class.java)

fun Context.obtainObjectIO() =
    obtain(ObjectIO::class.java) {
        obtain(JsonIO::class.java) {
            JsonIO(get(ModLoader::class.java))
        }
    }

fun Context.obtainJsonIO() =
    obtain(JsonIO::class.java) {
        JsonIO(get(ModLoader::class.java)).also {
            registerIfAbsent(it, ObjectIO::class.java)
        }
    }

fun Context.obtainJsonIO(modLoader: ModLoader): JsonIO =
    obtain(JsonIO::class.java) {
        JsonIO(modLoader).also {
            registerIfAbsent(it, ObjectIO::class.java)
        }
    }

fun Context.obtainVariables(path: String) =
    obtain(Variables::class.java) {
        obtain(ObjectIO::class.java) {
            obtain(JsonIO::class.java) {
                JsonIO(get(ModLoader::class.java)).apply {
                    objectMapper.registerVariablesModule()
                }
            }
        }.readObject(path, Variables::class.java) ?: Variables()
    }

fun Context.obtainVariables(path: String, modLoader: ModLoader) =
    obtain(Variables::class.java) {
        obtain(ObjectIO::class.java) {
            obtain(JsonIO::class.java) {
                JsonIO(modLoader).apply {
                    objectMapper.registerVariablesModule()
                }
            }
        }.readObject(path, Variables::class.java) ?: Variables()
    }

fun Context.obtainVariables(path: String, jsonIO: JsonIO) =
    obtain(Variables::class.java) {
        jsonIO.obtainVariables(path)
    }

fun Context.registerVariables(path: String, jsonIO: JsonIO) =
    register(jsonIO.obtainVariables(path))

inline fun <T: Disposable> GameContext.obtain(sharedObjectType: Class<T>,
                                              default: () -> T): T {
    var sharedObject = getOrNull(sharedObjectType)
    if (sharedObject != null) return sharedObject
    sharedObject = default()
    register(sharedObject, sharedObjectType)
    return sharedObject
}

inline fun buildGameContext(parent: ImmutableContext, actions: GameContext.() -> Unit): GameContext {
    val context = DefaultGameContext()
    context.registerCopy(parent)
    context.apply(actions)
    return context
}

inline fun buildGameContext(actions: GameContext.() -> Unit): GameContext = DefaultGameContext().apply(actions)

fun Context.obtainBinaryIO(modLoader: ModLoader): BinaryIO =
    obtain(BinaryIO::class.java) { BinaryIO(modLoader) }