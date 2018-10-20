package polaris.okapi.world

/**
 * Created by Killian Le Clainche on 2/26/2018.
 */

data class Vector(var x : Float, var y : Float, var z : Float) {
    @JvmOverloads constructor(x : Int, y : Int, z : Int = 0) : this(x.toFloat(), y.toFloat(), z.toFloat())
}