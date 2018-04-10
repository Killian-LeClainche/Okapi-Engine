package polaris.okapi.tests.starship.world.entity.mechanic.ships

import org.joml.Vector2d
import org.joml.Vector3d
import org.lwjgl.opengl.GL11
import polaris.okapi.render.Texture
import polaris.okapi.tests.starship.world.entity.ShipInfo
import polaris.okapi.tests.starship.world.entity.Starship
import polaris.okapi.world.Vector
import polaris.okapi.world.World

/**
 * Created by Killian Le Clainche on 2/25/2018.
 */

val MECHANIC_PLASMA_BATTLESHIP_INFO : ShipInfo = ShipInfo("Mechanic Plasma Battleship", 10, Vector2d(19.0, 0.0), 1000.0, 10000.0, Math.toRadians(45.0))


class MechPlasmaBattleship(world: World, position: Vector3d, rotation : Vector3d) : Starship(world, MECHANIC_PLASMA_BATTLESHIP_INFO.copy(), position, rotation) {

    val texture: Texture = world["mech-plasma-battleship", "resources/starship/ships/purple1.png"]

    override fun render(delta: Double) {
        texture.bind()

        GL11.glPushMatrix()
        GL11.glTranslated(position.x, position.y, position.z)
        GL11.glRotated(rotation.x, 0.0, 0.0, 1.0)
        GL11.glTranslated(shipInfo.massCenter.x, shipInfo.massCenter.y, 0.0)

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0f, 0f)
        GL11.glVertex3d(-128.0, -128.0, 0.0)
        GL11.glTexCoord2f(1f, 0f)
        GL11.glVertex3d(128.0, -128.0, 0.0)
        GL11.glTexCoord2f(1f, 1f)
        GL11.glVertex3d(128.0, 128.0, 0.0)
        GL11.glTexCoord2f(0f, 1f)
        GL11.glVertex3d(-128.0, 128.0, 0.0)
        GL11.glEnd()

        GL11.glPopMatrix()
    }

}