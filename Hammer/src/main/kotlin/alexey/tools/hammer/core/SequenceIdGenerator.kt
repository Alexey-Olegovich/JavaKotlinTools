package alexey.tools.hammer.core

class SequenceIdGenerator: IdGenerator {

    private var id = 0

    override fun next() = id++
}