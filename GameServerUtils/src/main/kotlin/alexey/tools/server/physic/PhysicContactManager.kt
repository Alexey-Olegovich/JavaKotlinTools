package alexey.tools.server.physic

import alexey.tools.common.level.DefaultContactManager
import com.badlogic.gdx.physics.box2d.*

class PhysicContactManager: DefaultContactManager<Contact>(), ContactListener {

    override fun beginContact(contact: Contact) {
        onContact(BEGIN, contact.fixtureA.getEntityId(), contact.fixtureB.getEntityId(), contact)
    }

    override fun endContact(contact: Contact) {
        onContact(END, contact.fixtureA.getEntityId(), contact.fixtureB.getEntityId(), contact)
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        onContact(PRE, contact.fixtureA.getEntityId(), contact.fixtureB.getEntityId(), contact)
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        onContact(POST, contact.fixtureA.getEntityId(), contact.fixtureB.getEntityId(), contact)
    }



    private fun Fixture.getEntityId(): Int = body.userData as Int



    companion object {
        const val BEGIN: Byte = 0
        const val PRE: Byte = 1
        const val POST: Byte = 2
        const val END: Byte = 3
    }
}