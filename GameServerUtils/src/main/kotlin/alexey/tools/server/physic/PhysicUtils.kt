package alexey.tools.server.physic

import alexey.tools.common.context.ImmutableVariables
import alexey.tools.server.models.EntityModel
import alexey.tools.server.models.ShapeModel
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef

fun Body.destroy() {
    world.destroyBody(this)
}

fun Body.rayCast(callback: RayCastCallback, to: Vector2) {
    world.rayCast(callback, position, to)
}

fun Body.createRevoluteJoint(b2: Body,
                             p1x: Float = 0F, p1y: Float = 0F,
                             p2x: Float = 0F, p2y: Float = 0F): Joint {
    val world = world
    val jointDef = RevoluteJointDef()
    jointDef.bodyA = this
    jointDef.bodyB = b2
    jointDef.localAnchorA.set(p1x, p1y)
    jointDef.localAnchorB.set(p2x, p2y)
    return world.createJoint(jointDef)
}

fun World.createBody(shapes: Iterable<ShapeModel>, active: Boolean = true,
                     x: Float = 0F, y: Float = 0F,
                     angle: Float = 0F,
                     properties: ImmutableVariables = ImmutableVariables.DEFAULT,
                     friction: Float = 0.1F, density: Float = 1.0F, restitution: Float = 0.0F,
                     sensor: Boolean = false,
                     category: Short = 1, mask: Short = -1, group: Short = 0): Body {

    val bodyDef = BodyDef()
    bodyDef.angularDamping = properties.get(PhysicConstants.ANGULAR_DAMPING)
    bodyDef.linearDamping = properties.get(PhysicConstants.LINEAR_DAMPING)
    bodyDef.fixedRotation = properties.get(PhysicConstants.FIXED_ROTATION)
    bodyDef.position.set(x, y)
    bodyDef.active = active
    bodyDef.bullet = properties.get(PhysicConstants.BULLET)
    bodyDef.angle = angle * MathUtils.degreesToRadians
    bodyDef.type = if (properties.get(PhysicConstants.DYNAMIC))
        BodyDef.BodyType.DynamicBody else
        BodyDef.BodyType.StaticBody
    val body = createBody(bodyDef)
    shapes.forEach { shape ->
        val fixtureDef = FixtureDef()
        fixtureDef.friction            = shape.properties.get(PhysicConstants.FRICTION   , friction   )
        fixtureDef.density             = shape.properties.get(PhysicConstants.DENSITY    , density    )
        fixtureDef.restitution         = shape.properties.get(PhysicConstants.RESTITUTION, restitution)
        fixtureDef.isSensor            = shape.properties.get(PhysicConstants.SENSOR     , sensor     )
        fixtureDef.filter.categoryBits = shape.properties.get(PhysicConstants.CATEGORY   , category   )
        fixtureDef.filter.maskBits     = shape.properties.get(PhysicConstants.MASK       , mask       )
        fixtureDef.filter.groupIndex   = shape.properties.get(PhysicConstants.GROUP      , group      )
        fixtureDef.shape = when(shape.type) {
            ShapeModel.CIRCLE  -> CircleShape ().apply { radius = shape.width; position = shape.position }
            ShapeModel.POLYGON -> PolygonShape().apply { set(shape.vertices) }
            ShapeModel.BOX     -> PolygonShape().apply { setAsBox(shape.width, shape.height, shape.position, 0F) }
            else               -> ChainShape  ().apply { createChain(shape.vertices) }
        }
        body.createFixture(fixtureDef)
        fixtureDef.shape.dispose()
    }
    return body
}

fun World.createBody(entityModel: EntityModel, userData: Any? = null, active: Boolean = true,
                     x: Float = entityModel.x, y: Float = entityModel.y,
                     angle: Float = entityModel.angle): Body =

    createBody(entityModel.shapes, active, x, y, angle, entityModel.properties,
        entityModel.properties.get(PhysicConstants.FRICTION),
        entityModel.properties.get(PhysicConstants.DENSITY),
        entityModel.properties.get(PhysicConstants.RESTITUTION),
        entityModel.properties.get(PhysicConstants.SENSOR),
        entityModel.properties.get(PhysicConstants.CATEGORY),
        entityModel.properties.get(PhysicConstants.MASK),
        entityModel.properties.get(PhysicConstants.GROUP)).also { it.userData = userData }