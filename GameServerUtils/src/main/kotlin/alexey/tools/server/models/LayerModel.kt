package alexey.tools.server.models

import alexey.tools.common.context.ImmutableVariables

class LayerModel(val entities: List<EntityModel> = emptyList(),
                 val properties: ImmutableVariables = ImmutableVariables.DEFAULT)