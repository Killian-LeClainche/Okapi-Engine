package polaris.okapi.util

import org.joml.Matrix4d
import org.joml.Vector2d
import org.joml.Vector4d

/**
 * Created by Killian Le Clainche on 4/11/2018.
 */

private val ZERO_CENTER = Vector4d(0.0, 0.0, 0.0, 1.0)

//topLeft - Top Left Corner of the Rectangle
//topRight - Top Right Corner of the Rectangle
//bottomRight - Bottom Right Corner of the Rectangle
//bottomLeft - Bottom Left Corner of the Rectangle
class Quad(val topLeft : Vector4d, val topRight : Vector4d, val bottomRight : Vector4d, val bottomLeft : Vector4d) {

    //Used for the contains point function
    var convex : Boolean = true

    constructor(center : Vector4d, width : Double, height : Double) : this(Vector4d(center), Vector4d(center), Vector4d(center), Vector4d(center)) {
        val halfwidth = width / 2
        val halfheight = height / 2

        topLeft.x -= halfwidth
        topLeft.y -= halfheight

        topRight.x += halfwidth
        topRight.y -= halfheight

        bottomRight.x += halfwidth
        bottomRight.y += halfheight

        bottomLeft.x -= halfwidth
        bottomLeft.y += halfheight
    }

    constructor(width : Double, height : Double) : this(ZERO_CENTER, width, height)

    fun set(quad : Quad) : Quad {
        topLeft.set(quad.topLeft)
        topRight.set(quad.topRight)
        bottomRight.set(quad.bottomRight)
        bottomLeft.set(quad.bottomLeft)
        return this
    }

    fun transform(matrix : Matrix4d) : Quad {
        matrix.transform(topLeft)
        matrix.transform(topRight)
        matrix.transform(bottomRight)
        matrix.transform(bottomLeft)
        return this
    }

    fun contains(x: Double, y: Double): Boolean {
        if(convex)
            return contains(x, y, topLeft, topRight, bottomRight) || contains(x, y, topLeft, bottomRight, bottomLeft)

        return contains(x, y, topLeft, topRight, bottomRight) xor contains(x, y, topLeft, bottomRight, bottomLeft)
    }

}