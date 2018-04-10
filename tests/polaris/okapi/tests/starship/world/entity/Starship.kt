package polaris.okapi.tests.starship.world.entity

import org.joml.Vector2d
import org.joml.Vector3d
import polaris.okapi.tests.starship.UPDATE_TIME_INTERVAL
import polaris.okapi.util.isEqual
import polaris.okapi.util.pi2
import polaris.okapi.util.piFractions
import polaris.okapi.world.Entity
import polaris.okapi.world.Vector
import polaris.okapi.world.World
import java.awt.geom.Point2D
import kotlin.math.PI

/**
 * Created by Killian Le Clainche on 2/25/2018.
 */

abstract class Starship(world: World, val shipInfo : ShipInfo, position : Vector3d, rotation : Vector3d) : Entity(world, position, rotation) {

    var destination : Vector3d? = null

    override fun update() {
        super.update()

        val realCenter = Vector3d(position.x + shipInfo.massCenter.x, position.y + shipInfo.massCenter.y, 0.0)

        if(destination != null) {
            val angle = Math.toDegrees(Math.atan2(destination!!.y - realCenter.y, destination!!.x - realCenter.x))

            shipInfo.thrust = Math.min(shipInfo.thrustConstant * UPDATE_TIME_INTERVAL + shipInfo.thrust, shipInfo.maxThrust)
            shipInfo.velocity.x = shipInfo.velocity.x + (shipInfo.thrust / shipInfo.mass - .5 * .1 * shipInfo.velocity.x * shipInfo.velocity.x * .2) * UPDATE_TIME_INTERVAL

            var travelDist = shipInfo.velocity.x * UPDATE_TIME_INTERVAL

            val distance = destination!!.distance(realCenter)

            if(!isEqual(angle, rotation.x)) {

                var turnTimeStep = shipInfo.turnAngle * UPDATE_TIME_INTERVAL


                var rotationRadius = travelDist / turnTimeStep

                /*if(rotationRadius * 2 > distance) {
                    rotationRadius = distance / 2
                    shipInfo.velocity.x = rotationRadius * turnTimeStep
                    travelDist = shipInfo.velocity.x * UPDATE_TIME_INTERVAL
                }*/

                val diffAngle = Math.toRadians(if(angle - rotation.x < -180) angle - rotation.x + 360 else (if(angle - rotation.x > 180) angle - rotation.x - 360 else angle - rotation.x))

                val turnRight = diffAngle >= 0

                val angleToCenter = Math.toRadians(if(turnRight)
                    rotation.x + 90
                else {
                    turnTimeStep = -turnTimeStep
                    rotation.x - 90
                })

                val px = position.x + rotationRadius * Math.cos(angleToCenter)
                val py = position.y + rotationRadius * Math.sin(angleToCenter)

                val subtract = (if(turnRight) -piFractions[2] else piFractions[2])

                val turnAngle = if(Math.abs(diffAngle) > Math.abs(turnTimeStep)) turnTimeStep else diffAngle

                travelDist -= Math.abs(turnAngle) * rotationRadius

                position.x = px + rotationRadius * StrictMath.cos(turnAngle + subtract + Math.toRadians(rotation.x))
                position.y = py + rotationRadius * StrictMath.sin(turnAngle + subtract + Math.toRadians(rotation.x))
                //System.out.println("$px $py ${Math.toDegrees(angleToCenter)} ${Math.toDegrees(turnAngle - piFractions[2] + Math.toRadians(rotation.x))}")
                rotation.x = (rotation.x + Math.toDegrees(turnAngle)) % 360
            }

            if(!isEqual(travelDist, 0.0)) {
                travelDist = Math.min(distance, travelDist)
                position.x = position.x + travelDist * StrictMath.cos(Math.toRadians(rotation.x))
                position.y = position.y + travelDist * StrictMath.sin(Math.toRadians(rotation.x))

                if(isEqual(rotation.x, angle, 1.0))
                    rotation.x = angle
            }

            if(distance < 2.0)
                destination = null
        }

        //destination = null
    }

    open fun moveTo(location: Point2D) {
        destination = Vector3d(location.x, location.y, 0.0)
    }
}