package polaris.okapi.tests.starship.world

import org.joml.Vector3d
import polaris.okapi.App
import polaris.okapi.options.Key
import polaris.okapi.render.Texture
import polaris.okapi.tests.starship.world.entity.mechanic.ships.MechPlasmaBattleship
import polaris.okapi.world.World
import java.awt.geom.Point2D

/**
 * Created by Killian Le Clainche on 2/23/2018.
 */

class StarshipWorld(application: App) : World(application) {

    var player: Player = Player(this, MechPlasmaBattleship(this, Vector3d(1000.0, 500.0, 0.0), Vector3d(0.0)))

    override fun update() {
        super.update()

        val moveKey = settings["action:move"] as Key

        if(moveKey.isClicked) {
            player.starship.moveTo(Point2D.Double(settings.mouse.x, settings.mouse.y))
        }

        player.starship.update()
    }

    override fun render(delta: Double) {
        super.render(delta)

        Texture.enable()

        player.starship.render(delta)

    }

}