package polaris.okapi.render

import jdk.nashorn.internal.objects.NativeFunction.function
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.stbi_image_free
import java.nio.ByteBuffer

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */

open class Texture @JvmOverloads constructor(var name: String, val width: Int, val height: Int, val composite: Int, var mipmapMaxLevel: Int, val image: ByteBuffer, var id: Int = glGenTextures()) {

    fun use(function: () -> Unit) {
        glBindTexture(GL_TEXTURE_2D, this.id)
        function()
    }

    fun bind() = glBindTexture(GL_TEXTURE_2D, id)

    fun destroy() {
        if (id != 0)
            GL11.glDeleteTextures(id)

        id = 0
    }

    fun close() {
        destroy()
        stbi_image_free(image)
    }

    companion object {
        fun enable() = glEnable(GL_TEXTURE_2D)
        fun disable() = glDisable(GL_TEXTURE_2D)
    }
}

class TextureArray constructor(texture: Texture, texCoords: ByteBuffer) : Texture(texture.name, texture.width, texture.height, texture.composite, texture.mipmapMaxLevel, texture.image, texture.id) {

    var textures: Array<TexCoord?>
        private set

    init {
        textures = arrayOfNulls(texCoords.int)

        var index = 0
        while (texCoords.hasRemaining()) {
            textures[index] = TexCoord(texCoords.float, texCoords.float, texCoords.float, texCoords.float)
            index++
        }
    }

}

data class TexCoord(val minU: Float, val minV: Float, val maxU: Float, val maxV: Float)
