package polaris.okapi.render

import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.GL20C.*
import polaris.okapi.options.Settings
import polaris.okapi.util.readFileAsString
import java.io.File

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */

const val QUAD_POINTS = 6
const val COORDS_UV = 5

class RenderManager(val settings: Settings) {

    //public static final Shader POS;
    //public static final Shader POS_COLOR;
    //public static final Shader POS_TEXTURE;
    //public static final Shader POS_COLOR_TEXTURE;

    val posShader: Shader = Shader()
    val posColorShader: Shader = Shader()
    val posTextureShader: Shader = Shader()
    val posColorTextureShader: Shader = Shader()

    init {
        posShader.vertexShaderId = loadShader(File("resources/shaders/pos.vert"), GL_VERTEX_SHADER)
        posShader.fragmentShaderId = loadShader(File("resources/shaders/pos.frag"), GL_FRAGMENT_SHADER)

        posShader.link()

        posColorShader.vertexShaderId = loadShader(File("resources/shaders/pos_color.vert"), GL_VERTEX_SHADER)
        posColorShader.fragmentShaderId = loadShader(File("resources/shaders/pos_color.frag"), GL_FRAGMENT_SHADER)

        posColorShader.link()
    }

    /*@JvmOverloads
    fun print(font: Font, text: String, x: Float, y: Float, z: Float = 0.0f, height: Float): VBO? {
        val scale = stbtt_ScaleForPixelHeight(font.info, height)
        val vboBuffer = VBOBuffer((text.length * QUAD_POINTS * COORDS_UV).toLong())

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
    }*/

    fun loadShader(filename: File, shaderType: Int): Int {
        var shader = 0
        try {
            shader = glCreateShader(shaderType)

            if (shader == 0) return 0

            glShaderSource(shader, readFileAsString(filename))

            glCompileShader(shader)

            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
                throw RuntimeException("Error creating shader: " + getLogInfo(shader))

            return shader
        } catch (exc: Exception) {
            if(shader != 0)
                glDeleteShader(shader)

            println(exc.message)
            return -1
        }

    }

    private fun getLogInfo(obj: Int): String {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB))
    }

}