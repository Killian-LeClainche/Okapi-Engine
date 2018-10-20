package polaris.okapi.tests.starship.world.entity

import org.joml.Vector2d

/**
 * Created by Killian Le Clainche on 2/26/2018.
 */

data class ShipInfo(val shipName : String, var mass : Long, var massCenter : Vector2d, var thrustConstant : Double, var maxThrust : Double, var turnAngle : Double) {

    var velocity : Vector2d = Vector2d(0.0)
    var thrust : Double = 5.0

}