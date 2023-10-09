package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables

class LevelModel(val layers: List<LayerModel> = emptyList(),
                 val properties: ImmutableVariables = ImmutableVariables.DEFAULT,
                 val width: Int = 0,
                 val height: Int = 0) {

    companion object {
        val DEFAULT = LevelModel()
    }
}