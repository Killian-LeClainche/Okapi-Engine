package polaris.okapi.render

import polaris.okapi.options.Settings
import org.lwjgl.stb.STBTTAlignedQuad
import polaris.okapi.util.scale
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryStack.stackPush

const val QUAD_POINTS = 6
const val COORDS_UV = 5

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */

class RenderManager(private val settings: Settings) {

    @JvmOverloads
    fun print(font: Font, text: String, x: Float, y: Float, z: Float = 0.0f, height: Float): VBO? {
        val scale = stbtt_ScaleForPixelHeight(font.info, height)
        val vboBuffer = VBOBuffer(text.length * QUAD_POINTS * COORDS_UV)

        stackPush().use {
            val nextX = it.floats(x)
            val nextY = it.floats(y)

            val quad = STBTTAlignedQuad.mallocStack(it)

            var currentX: Float

            var x0: Float
            var x1: Float
            var y0: Float
            var y1: Float

            for(i in 0 until text.length) {
                currentX = nextX[0]

                stbtt_GetPackedQuad(font.chardata, font.width, font.height, text[i].toInt() - 32, nextX, nextY, quad, true)

                nextX.put(0, scale(currentX, nextX[0], scale))

                if(i + 1 < text.length)
                    nextX.put(0, nextX[0] + stbtt_GetCodepointKernAdvance(font.info, text[i].toInt() - 32, text[i + 1].toInt() - 32))

                x0 = scale(currentX, quad.x0(), scale)
                x1 = scale(currentX, quad.x1(), scale)
                y0 = scale(nextY[0], quad.y0(), scale)
                y1 = scale(nextY[0], quad.y1(), scale)

                vboBuffer.rectUV(x0, y0, x1, y1, z, TexCoord(quad.s0(), quad.t0(), quad.s1(), quad.t1()))
            }
        }

        return null
    }

}