package alexey.tools.client.viewport

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

fun Camera.toWorld(x: Float, y: Float): Vector2 {
    val result = Vector3(x, y, 0f)
    this.unproject(result)
    return Vector2(result.x, result.y)
}

fun Camera.toWorld(point: Vector2): Vector2 = toWorld(point.x, point.y)

fun Camera.getWorldCorner(): Vector2 =
    Vector2(position.x - viewportWidth / 2f, position.y - viewportHeight / 2f)

fun Camera.getCenter(): Vector2 =
    Vector2(viewportWidth / 2f, viewportHeight / 2f)