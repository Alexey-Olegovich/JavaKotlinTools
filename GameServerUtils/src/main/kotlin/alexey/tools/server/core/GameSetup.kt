package alexey.tools.server.core

interface GameSetup <T: Processor> {
    fun setup(game: Game<T>) {}
}