package polaris.okapi.world

/**
 * Created by Killian Le Clainche on 2/26/2018.
 */

data class Vector(var x : Double, var y : Double, var z : Double) {
    @JvmOverloads constructor(x : Int, y : Int, z : Int = 0) : this(x.toDouble(), y.toDouble(), z.toDouble())
}