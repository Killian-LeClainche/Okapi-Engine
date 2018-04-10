package polaris.okapi.world

import org.joml.Vector3d

/**
 * Created by Killian Le Clainche on 2/25/2018.
 */

abstract class Entity(protected val world: World, protected var position : Vector3d, protected var rotation : Vector3d) {

    var ticksExisted : Long = 0

    open fun update() {
        ticksExisted++
    }

    abstract fun render(delta: Double)

}