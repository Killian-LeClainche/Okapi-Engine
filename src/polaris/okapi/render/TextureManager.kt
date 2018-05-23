package polaris.okapi.render

import org.lwjgl.opengl.GL11
import polaris.okapi.options.IntSetting
import polaris.okapi.options.Settings
import polaris.okapi.util.ioResourceToByteBuffer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL
import org.lwjgl.stb.STBImage.*
import org.lwjgl.stb.STBImageResize.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.rpmalloc.RPmalloc.rpfree
import org.lwjgl.system.rpmalloc.RPmalloc.rpmalloc
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.opengl.GL42.glTexStorage2D
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.util.HashMap
import kotlin.experimental.and
import kotlin.math.roundToInt

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */
class TextureManager(val settings: Settings) {

    var textures: MutableMap<String, Texture> = HashMap()
        set(value) = field.forEach { reinitTexture(it.value) }

    operator fun get(value: String) : Texture {
        return textures[value]!!
    }

    operator fun get(value: List<String>) : List<Texture> {
        return value.filter { textures.containsKey(it) }.map { textures[it]!! }
    }

    operator fun get(value: Array<String>) : List<Texture> {
        return value.filter { textures.containsKey(it) }.map { textures[it]!! }
    }

    operator fun set(key: String, value: String) {
        val mipmap = (settings["mipmap"] as IntSetting?)?.value ?: 0

        if(!textures.containsKey(key))
            genTexture(key, File(value), mipmap)
    }

    operator fun set(key: String, value: Array<String>) {
        val mipmap = (settings["mipmap"] as IntSetting?)?.value ?: 0

        if(!textures.containsKey(key))
            genTexture(key, File(value[0]), File(value[1]), mipmap)
    }

    operator fun set(key: String, value: Texture) {
        if(textures.containsKey(key) && textures[key] != value)
            textures[key]!!.close()

        textures[key] = value
    }

    operator fun set(key: List<String>, value: List<Texture>) {
        key.forEachIndexed { index, _key ->
            this[_key] = value[index]
        }
    }

    operator fun set(key: Array<String>, value: Array<Texture>) {
        key.forEachIndexed { index, _key ->
            this[_key] = value[index]
        }
    }

    operator fun plusAssign(value: Texture) {
        reinitTexture(value)
    }

    operator fun plusAssign(value: List<Texture>) {
        val mipmap = (settings["mipmap"] as IntSetting?)?.value ?: 0

        value.forEach { reinitTexture(it) }
    }

    operator fun plusAssign(value: Array<Texture>) {
        val mipmap = (settings["mipmap"] as IntSetting?)?.value ?: 0

        value.forEach { reinitTexture(it) }
    }

    operator fun plusAssign(value: Array<String>) {
        val mipmap = (settings["mipmap"] as IntSetting?)?.value ?: 0

        if(value.size == 3)
            genTexture(value[0], File(value[1]), File(value[2]), mipmap)
        else
            genTexture(value[0], File(value[1]), mipmap)
    }

    operator fun minusAssign(value: Texture) {
        clearTexture(value)
    }

    operator fun minusAssign(value: String) {
        val texture = textures[value]

        if(texture != null)
            clearTexture(texture)
    }

    operator fun minusAssign(value: List<Any>) {
        getTextures(value).forEach {
            clearTexture(it)
        }
    }

    operator fun minusAssign(value: Array<Any>) {
        getTextures(value).forEach {
            clearTexture(it)
        }
    }

    operator fun remAssign(value: Texture) {
        textures.filterNot { it.value.id == value.id }
                .forEach { clearTexture(it.value) }
    }

    operator fun remAssign(value: String) {
        if(textures.containsKey(value))
            textures.filterNot { it.key == value }
                    .forEach { clearTexture(it.value) }
    }

    operator fun remAssign(value: List<Any>) {
        val list = getTextures(value)
        textures.filterNot { list.any { it2 -> it2.id == it.value.id } }
                .forEach { clearTexture(it.value) }
    }

    operator fun remAssign(value: Array<Any>) {
        val list = getTextures(value)
        textures.filterNot { list.any { it2 -> it2.id == it.value.id } }
                .forEach { clearTexture(it.value) }
    }

    fun getTextures(list: List<Any>) : List<Texture> {
        return list.filter {
            if(it is Texture)
                true
            else
                textures.containsKey(it)
        }.map {
            it as? Texture ?: textures[it]!!
        }
    }

    fun getTextures(list: Array<Any>) : List<Texture> {
        return list.filter {
            if(it is Texture)
                true
            else
                textures.containsKey(it)
        }.map {
            it as? Texture ?: textures[it]!!
        }
    }

    @JvmOverloads
    fun genTexture(textureName: String, textureFile: File, mipmapMaxLevel: Int = 1): Texture? {
        val image = ioResourceToByteBuffer(textureFile) ?: return null
        var texture: Texture? = null

        stackPush().use {
            val width = it.mallocInt(1)
            val height = it.mallocInt(1)
            val comp = it.mallocInt(1)

            if(!stbi_info_from_memory(image, width, height, comp)) {
                System.err.println("Could not load info for image!")
                System.err.println(stbi_failure_reason())
                return null;
            }

            val data = stbi_load_from_memory(image, width, height, comp, 0)
            if(data == null) {
                System.err.println("Could not load image!")
                System.err.println(stbi_failure_reason())
                return null;
            }

            texture = Texture(textureName, width[0], height[0], comp[0], mipmapMaxLevel, data)

            genTexture(texture!!)

            textures.put(textureName, texture!!)
        }

        return texture
    }

    @JvmOverloads
    fun genTexture(textureName: String, width : Int, height : Int, comp : Int, data : ByteBuffer, mipmapMaxLevel: Int = 1) : Texture {
        val texture = Texture(textureName, width, height, comp, mipmapMaxLevel, data)

        genTexture(texture)

        textures.put(textureName, texture)

        return texture
    }

    fun genTexture(texture: Texture) {
        texture.bind()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, texture.mipmapMaxLevel)

        val format = if(texture.composite == 4)
            GL_RGBA
        else
            GL_RGB

        glTexImage2D(GL_TEXTURE_2D, 0, format, texture.width, texture.height, 0, format, GL_UNSIGNED_BYTE, texture.image)

        var pixelsIn : ByteBuffer? = texture.image
        var widthIn = texture.width
        var heightIn = texture.height
        var mipmapLevel = 1

        while(mipmapLevel <= texture.mipmapMaxLevel && widthIn > 1 && heightIn > 1) {
            val widthOut = (widthIn shr 1).coerceAtLeast(2)
            val heightOut = (heightIn shr 1).coerceAtLeast(2)
            val pixelsOut = rpmalloc((widthOut * heightOut * texture.composite).toLong())

            if(pixelsOut != null && pixelsIn != null) {
                stbir_resize_uint8_generic(pixelsIn, widthIn, heightIn, widthIn * texture.composite,
                        pixelsOut, widthOut, heightOut, widthOut * texture.composite,
                        texture.composite, if (texture.composite == 4) 3 else STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
                        STBIR_EDGE_WRAP, STBIR_FILTER_MITCHELL, STBIR_COLORSPACE_SRGB)

                glTexImage2D(GL_TEXTURE_2D, mipmapLevel++, format, widthOut, heightOut, 0, format, GL_UNSIGNED_BYTE, pixelsOut)
            }
            else
                System.err.println("Error creating ByteBuffer of size $widthOut $heightOut and composite ${texture.composite}!")

            if (pixelsIn != null && mipmapLevel > 1)
                rpfree(pixelsIn)

            pixelsIn = pixelsOut
            widthIn = widthOut
            heightIn = heightOut
        }

        if(pixelsIn != null && mipmapLevel > 1)
            rpfree(pixelsIn)

        /*texture.bind()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        if (texture.mipmapMaxLevel > 1)
        {
            glTexStorage2D(GL_TEXTURE_2D, 5, GL_RGBA8, texture.width, texture.height);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, texture.width, texture.height, GL_RGBA, GL_UNSIGNED_BYTE, texture.image);
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        }
        else
        {
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.width, texture.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.image);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        }*/

        this[texture.name] = texture
    }

    @JvmOverloads
    fun genTexture(textureName: String, textureFile: File, arrayFile: File, mipmapMaxLevel: Int = 1): TextureArray? {
        val buffer= ioResourceToByteBuffer(arrayFile) ?: return null

        val baseTexture = genTexture(textureName, textureFile, mipmapMaxLevel) ?: return null

        val texture = TextureArray(baseTexture, buffer)
        textures.put(textureName, texture)

        return texture
    }

    fun reinitTexture(texture: Texture) {
        texture.destroy()
        texture.id = glGenTextures()

        genTexture(texture)

        this[texture.name] = texture
    }

    fun clear() {
        textures.forEach { it.value.close() }
        textures.clear()
    }

    fun clearTexture(texture: Texture) {
        texture.close()
        textures.remove(texture.name)
    }

}
