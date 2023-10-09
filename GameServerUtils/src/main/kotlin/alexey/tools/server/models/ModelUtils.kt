package alexey.tools.server.models

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.collections.convert
import alexey.tools.common.collections.forEachInt
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

fun TiledMap.toLevel(blankEntities: List<EntityModel>, scale: Float = 1F): LevelModel {
    val layersModels = ObjectList<LayerModel>(layers.size)
    val scaledTileWidth =  tileWidth  * scale
    val scaledTileHeight = tileHeight * scale
    val scaledTileHalfWidth = scaledTileWidth / 2F
    val scaledTileHalfHeight = scaledTileHeight / 2F
    for (i in layers.indices) {
        val layer = layers[i]
        val entities: ObjectList<EntityModel>
        if (layer.width == 0) {
            entities = ObjectList<EntityModel>(layer.objects.size)
            layer.objects.forEach { o ->
                val width = o.width * scale
                val height = o.height * scale
                val point = Vector2(width / 2F, height / 2F)
                    .rotateRad(-o.rotation * MathUtils.degreesToRadians)
                    .add(o.x * scale - scaledTileHalfWidth, (tileHeight - o.y) * scale - scaledTileHalfHeight)
                entities.unsafeAdd(blankEntities[o.gId - 1]
                    .with(point.x, point.y, width, height, -o.rotation, i, o.type, o.properties))
            }
        } else {
            entities = ObjectList<EntityModel>(layer.width * layer.height)
            if (layer.data.isNotEmpty) {
                val xEnd = layer.width * scaledTileWidth - scaledTileHalfWidth
                var y = -scaledTileHalfHeight
                var x = -scaledTileHalfWidth
                layer.data.forEachInt { gId ->
                    if (gId != 0) {
                        val blankEntity = blankEntities[gId - 1]
                        val halfWidth = blankEntity.width / 2F
                        val halfHeight = blankEntity.height / 2F
                        entities.unsafeAdd(blankEntity.with(x + halfWidth, y + halfHeight))
                    }
                    x += scaledTileWidth
                    if (x == xEnd) {
                        x = -scaledTileHalfWidth; y -= scaledTileHeight
                    }
                }
            }
            layer.chunks.forEach { chunk ->
                val startX = chunk.x * scaledTileWidth - scaledTileHalfWidth
                val xEnd = startX + chunk.width * scaledTileWidth
                var y = -chunk.y * scaledTileHeight - scaledTileHalfHeight
                var x = startX
                chunk.data.forEachInt { gId ->
                    if (gId != 0) {
                        val blankEntity = blankEntities[gId - 1]
                        val halfWidth = blankEntity.width / 2F
                        val halfHeight = blankEntity.height / 2F
                        entities.unsafeAdd(blankEntity.with(x + halfWidth, y + halfHeight, i))
                    }
                    x += scaledTileWidth
                    if (x == xEnd) { x = startX; y -= scaledTileHeight }
                }
            }
        }
        layersModels.unsafeAdd(LayerModel(entities, layer.properties))
    }
    return LevelModel(layersModels, properties, width, height)
}

fun TiledMap.toLevel(tiledSets: Iterable<TiledSet>, scale: Float = 1F): LevelModel =
    toLevel(tiledSets.toBlankEntities(scale), scale)

fun Iterable<TiledSet>.toBlankEntities(scale: Float = 1F): List<EntityModel> {
    val blankEntities = ObjectList<EntityModel>(sumOf { it.tileCount })
    var fromIndex = 0L
    forEach { tileSet ->
        blankEntities.unsafeAddAll(tileSet.toBlankEntities(scale, fromIndex))
        fromIndex += tileSet.tileCount
    }
    return blankEntities
}

fun TiledSet.toBlankEntities(scale: Float = 1F, firstIndex: Long = 0): List<EntityModel> {
    val blankEntities = ObjectList<EntityModel>(tileCount)
    blankEntities.setSize(blankEntities.capacity())
    val scaledTileWidth =  tileWidth  * scale
    val scaledTileHeight = tileHeight * scale
    val scaledTileHalfWidth = scaledTileWidth / 2F
    val scaledTileHalfHeight = scaledTileHeight / 2F
    tiles.forEach { tile ->
        blankEntities.justSet(tile.id, EntityModel.newInstance(
            scaledTileWidth, scaledTileHeight, makeType(contentClass, tile.type, tile.id), firstIndex + tile.id,
            tile.objectGroup.objects.convert { tiledShape ->
                tiledShape.toShapeModel(scaledTileHalfWidth, scaledTileHalfHeight, scale) }, tile.properties))
    }
    for (index in blankEntities.indices) if (blankEntities[index] == null)
        blankEntities.justSet(index, EntityModel.newInstance(scaledTileWidth, scaledTileHeight,
            makeType(contentClass, index), firstIndex + index))
    return blankEntities
}

fun TiledShape.toShapeModel(tileHalfWidth: Float, tileHalfHeight: Float, scale: Float = 1F): ShapeModel = when {
    ellipse -> {
        val radius = width * scale / 2F
        ShapeModel.newCircle(x * scale + radius - tileHalfWidth,
            tileHalfHeight - (y + height) * scale + radius,
            radius, properties)
    }

    polygon.isNotEmpty() ->
        ShapeModel.newPolygon(polygon.convert(x, y, tileHalfWidth, tileHalfHeight, scale), properties)

    width != 0.0F -> {
        val boxHalfWidth = width * scale / 2F
        val boxHalfHeight = height * scale / 2F
        ShapeModel.newBox(x * scale + boxHalfWidth - tileHalfWidth,
            tileHalfHeight - (y + height) * scale + boxHalfHeight,
            boxHalfWidth, boxHalfHeight, properties)
    }

    else ->
        ShapeModel.newPolyline(polyline.convert(x, y, tileHalfWidth, tileHalfHeight, scale), properties)
}



private fun makeType(setType: String, index: Int): String =
    if (setType.isEmpty()) "" else setType + index

private fun makeType(setType: String, objType: String, index: Int): String =
    if (setType.isEmpty()) { objType } else { if (objType.isEmpty()) setType + index else setType + objType }

private fun Array<Vector2>.convert(x: Float, y: Float, halfWidth: Float, halfHeight: Float, scale: Float): Array<Vector2> {
    val realX = x * scale - halfWidth
    val realY = halfHeight - y * scale
    return Array(size) { Vector2(get(it)).scl(scale, -scale).add(realX, realY) }
}