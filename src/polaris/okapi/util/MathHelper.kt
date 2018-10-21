package polaris.okapi.util

import net.jafama.FastMath
import net.jafama.StrictFastMath
import org.joml.Vector4d
import kotlin.math.PI

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */

val log2 = 1 / Math.log(2.0)
const val pi2 = 2 * PI
val piFractions : Array<Double> = arrayOf(PI * 2.0, PI, PI / 2.0, PI / 3.0, PI / 4.0, PI / 5.0, PI / 6.0)

fun inBounds(mx: Double, my: Double, bounds: Vector4d): Boolean {
    return bounds.x <= mx && bounds.z >= mx && bounds.y <= my && bounds.w >= my
}

/**
 * Retrieves a linear number from one number to another
 *
 * @param value    : The current value
 * @param toValue  : The value for which value wants to become
 * @param modifier : How fast for the value to get there (in seconds)
 *
 * @return either (value - modifier) or (value + modifier) with a cap at toValue
 */
fun getLinearValue(value: Double, toValue: Double, modifier: Double): Double {
    return if (value > toValue) Math.max(toValue, value - modifier) else Math.min(toValue, value + modifier)
}

/**
 * Retrieves a linear number from one number to another
 *
 * @param value    : The current value
 * @param toValue  : The value for which value wants to become
 * @param modifier : How fast for the value to get there (in seconds)
 * @param delta    : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
 *
 * @return either (value - modifier * delta) or (value + modifier * delta) with a cap at toValue
 */
fun getLinearValue(value: Double, toValue: Double, modifier: Double, delta: Double): Double {
    return if (value > toValue) Math.max(toValue, value - modifier * delta) else Math.min(toValue, value + modifier * delta)
}

/**
 * Retrieves a linear number from one number to another
 *
 * @param value    : The current value
 * @param toValue  : The value for which value wants to become
 * @param modifier : How fast for the value to get there (in seconds)
 *
 * @return either (value - modifier) or (value + modifier) with a cap at toValue
 */
fun getLinearDistance(value: Double, toValue: Double, modifier: Double): Double {
    return if (value > toValue) Math.max(toValue, value - modifier) - value else Math.min(toValue, value + modifier) - value
}

/**
 * Retrieves a linear number from one number to another
 *
 * @param value    : The current value
 * @param toValue  : The value for which value wants to become
 * @param modifier : How fast for the value to get there (in seconds)
 * @param delta    : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
 *
 * @return either (value - modifier * delta) or (value + modifier * delta) with a cap at toValue
 */
fun getLinearDistance(value: Double, toValue: Double, modifier: Double, delta: Double): Double {
    return if (value > toValue) Math.max(toValue, value - modifier * delta) - value else Math.min(toValue, value + modifier * delta) - value
}

fun getExpValue(value: Double, toValue: Double, modifier: Double): Double {
    return value + (toValue - value) / modifier
}

fun getExpValue(value: Double, toValue: Double, modifier: Double, delta: Double): Double {
    return value + (toValue - value) / modifier * delta
}

fun getExpDistance(value: Double, toValue: Double, modifier: Double): Double {
    return (toValue - value) / modifier
}

fun getExpDistance(value: Double, toValue: Double, modifier: Double, delta: Double): Double {
    return (toValue - value) / modifier * delta
}

fun isEqual(value: Double, value1: Double): Boolean {
    return Math.abs(value - value1) < .0001
}

fun isEqual(value: Double, value1: Double, tolerance: Double): Boolean {
    return Math.abs(value - value1) < tolerance
}

fun random(maxValue: Int): Int {
    return (Math.random() * (maxValue + 1)).toInt()
}

fun random(maxValue: Float): Float {
    return (Math.random() * maxValue).toFloat()
}

fun random(maxValue: Double): Double {
    return Math.random() * maxValue
}

fun random(minValue: Int, maxValue: Int): Int {
    return StrictFastMath.round(Math.random() * (maxValue - minValue) + minValue).toInt()
}

fun random(minValue: Float, maxValue: Float): Float {
    return (Math.random() * (maxValue - minValue)).toFloat() + minValue
}

fun random(minValue: Double, maxValue: Double): Double {
    return Math.random() * (maxValue - minValue) + maxValue
}

fun clamp(max: Double, value: Double): Double {
    if(0.0 > value) return 0.0
    return if(max < value) max else value
}

fun clamp(max: Int, value: Int): Int {
    if(0 > value) return 0
    return if(max < value) max else value
}

fun clamp(min: Double, max: Double, value: Double): Double {
    if (min > value) return min
    return if (max < value) max else value
}

fun clamp(min: Int, max: Int, value: Int) : Int {
    if(min > value) return min
    return if (max < value) max else value
}

fun log2(value: Double): Double {
    return FastMath.logQuick(value) * log2
}

fun log(base: Double, value: Double): Double {
    return FastMath.logQuick(value) / FastMath.logQuick(base)
}

fun pythagoreon(a: Double, b: Double): Double {
    return FastMath.sqrtQuick(a * a + b * b)
}

fun pythagoreon(a: Double, b: Double, c: Double): Double {
    return FastMath.sqrtQuick(a * a + b * b + c * c)
}

fun scale(center: Float, offset: Float, scale: Float): Float = (offset - center) * scale + center
fun scale(center: Double, offset: Double, scale: Double): Double = (offset - center) * scale + center