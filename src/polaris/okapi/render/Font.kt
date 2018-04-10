package polaris.okapi.render

import jdk.nashorn.internal.objects.NativeArray.forEach
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTBakedChar
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.rpmalloc.RPmalloc
import org.lwjgl.system.rpmalloc.RPmalloc.rpfree
import java.nio.ByteBuffer
import polaris.okapi.util.scale
import java.awt.SystemColor.text

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */

data class Font @JvmOverloads constructor(val name: String, val info: STBTTFontinfo, val width: Int, val height: Int, val oversample: Int, val bitmap: ByteBuffer, var id: Int = glGenTextures()) {

    var ascent: Int = 0
        private set
    var descent: Int = 0
        private set
    var lineGap: Int = 0
        private set
    val chardata: STBTTPackedchar.Buffer = STBTTPackedchar.malloc(96)

    init {
        stackPush().use {
            val fontAscent = it.mallocInt(1)
            val fontDescent = it.mallocInt(1)
            val fontLineGap = it.mallocInt(1)

            stbtt_GetFontVMetrics(info, fontAscent, fontDescent, fontLineGap)

            ascent = fontAscent[0]
            descent = fontDescent[0]
            lineGap = fontLineGap[0]
        }
    }

    fun bind() = glBindTexture(GL_TEXTURE_2D, id)

    fun getWidth(text: String, scale: Float): Float = getWidth(text) * scale

    fun getWidth(text: String): Float {
        var length = 0f
        text.chars().forEach {
            length += chardata[it - 32].xadvance()
        }
        return length
    }

    fun destroy() {
        if(id != 0)
            glDeleteTextures(id)

        id = 0
    }

    fun close() {
        destroy()
        info.free()
        chardata.free()
        rpfree(bitmap)
    }

    /*fun draw(text: String, x: Float, y: Float, z: Float, scale: Float): VBO? {
        var x = x
        //int bufferSize = text.length() * 6 * 5;
        //VBOBuffer vboBuffer = new VBOBuffer(bufferSize);
        //IBOBuffer iboBuffer = new IBOBuffer(bufferSize);
        val quad = STBTTAlignedQuad.malloc()
        //VBO vbo;
        xBuffer.put(0, 0f)
        yBuffer.put(0, 0f)

        GL11.glPushMatrix()

        GL11.glTranslated(x.toDouble(), y.toDouble(), z.toDouble())
        GL11.glBegin(GL11.GL_QUADS)

        var c: Char
        var x0: Float
        var y0: Float
        var x1: Float
        var y1: Float
        for (i in 0 until text.length) {
            c = text[i]
            if (c == '\n') {
                xBuffer.put(0, 0f)
                yBuffer.put(0, yBuffer.get(0) + size)
                continue
            }

            x = xBuffer.get(0)
            stbtt_GetBakedQuad(fontChardata, fontWidth, fontHeight, c.toInt() - 32, xBuffer, yBuffer, quad, true)

            x0 = quad.x0()
            y0 = quad.y0()
            x1 = quad.x1()
            y1 = quad.y1()

            x1 = x0 + (x1 - x0) * scale
            y0 = y1 + (y0 - y1) * scale

            xBuffer.put(0, x + (xBuffer.get(0) - x) * scale)

            GL11.glTexCoord2d(quad.s0().toDouble(), quad.t1().toDouble())
            GL11.glVertex3d(x0.toDouble(), y1.toDouble(), z.toDouble())
            GL11.glTexCoord2d(quad.s1().toDouble(), quad.t1().toDouble())
            GL11.glVertex3d(x1.toDouble(), y1.toDouble(), z.toDouble())
            GL11.glTexCoord2d(quad.s1().toDouble(), quad.t0().toDouble())
            GL11.glVertex3d(x1.toDouble(), y0.toDouble(), z.toDouble())
            GL11.glTexCoord2d(quad.s0().toDouble(), quad.t0().toDouble())
            GL11.glVertex3d(x0.toDouble(), y0.toDouble(), z.toDouble())

            /*vboBuffer.addVertex(x0, y1, z);
			vboBuffer.addVertex(x1, y1, z);
			vboBuffer.addVertex(x0, y0, z);

			vboBuffer.addVertex(x0, y0, z);
			vboBuffer.addVertex(x1, y1, z);
			vboBuffer.addVertex(x1, y0, z);*/

            /*vboBuffer.addTextureVertex(x0, y1, z, quad.s0(), quad.t1());
			vboBuffer.addTextureVertex(x1, y1, z, quad.s1(), quad.t1());
			vboBuffer.addTextureVertex(x0, y0, z, quad.s0(), quad.t0());

			vboBuffer.addTextureVertex(x0, y0, z, quad.s0(), quad.t0());
			vboBuffer.addTextureVertex(x1, y1, z, quad.s1(), quad.t1());
			vboBuffer.addTextureVertex(x1, y0, z, quad.s1(), quad.t0());*/
        }

        GL11.glEnd()

        GL11.glPopMatrix()
        quad.free()

        //iboBuffer.shrinkVBO(vboBuffer, VBO.POS_TEXTURE_STRIDE);

        //vbo = VBO.createStaticVBO(GL11.GL_TRIANGLES, VBO.POS_TEXTURE, VBO.POS_TEXTURE_STRIDE, VBO.POS_TEXTURE_OFFSET, vboBuffer);
        //return vbo;
        return null
    }*/

}