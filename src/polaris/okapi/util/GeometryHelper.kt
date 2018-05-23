package polaris.okapi.util

import org.joml.Vector2d
import org.joml.Vector4d

/**
 * Created by Killian Le Clainche on 4/13/2018.
 */

fun contains(x : Double, y : Double, p1 : Vector4d, p2 : Vector4d, p3 : Vector4d) : Boolean {

    val v0 = Vector2d(p3.x - p1.x, p3.y - p1.y)
    val v1 = Vector2d(p2.x - p1.x, p2.y - p1.y)
    val v2 = Vector2d(x - p1.x, y - p1.y)

    val dot00 = v0.dot(v0)
    val dot01 = v0.dot(v1)
    val dot02 = v0.dot(v2)
    val dot11 = v1.dot(v1)
    val dot12 = v1.dot(v2)

    val invDenom = 1 / (dot00 * dot11 - dot01 * dot01)
    val u = (dot11 * dot02 - dot01 * dot12) * invDenom
    val v = (dot00 * dot12 - dot01 * dot02) * invDenom

    return (u >= 0) && (v >= 0) && (u + v < 1)
}