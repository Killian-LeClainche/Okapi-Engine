package polaris.okapi.render

import polaris.okapi.options.IntSetting
import polaris.okapi.options.Settings
import polaris.okapi.util.ioResourceToByteBuffer
import polaris.okapi.util.log
import net.jafama.FastMath.ceil
import net.jafama.FastMath.pow
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.*
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.rpmalloc.RPmalloc
import org.lwjgl.system.rpmalloc.RPmalloc.rpmalloc
import java.io.File
import java.util.HashMap
import kotlin.math.roundToInt

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */

class FontManager(private val settings: Settings) {
    
    var fonts: MutableMap<String, Font> = HashMap()

    operator fun get(value: String) : Font? = fonts[value]

    operator fun get(value: List<String>) : List<Font> = value.filter { fonts.containsKey(it) }.map { fonts[it]!! }

    operator fun get(value: Array<String>) : List<Font> = value.filter { fonts.containsKey(it) }.map { fonts[it]!! }

    operator fun set(key: String, value: Font) {
        if(fonts.containsKey(key) && fonts[key] != value)
            fonts[key]!!.close()

        fonts[key] = value
    }

    operator fun set(key: List<String>, value: List<Font>) = key.forEachIndexed { index, _key -> this[_key] = value[index] }

    operator fun set(key: Array<String>, value: Array<Font>) = key.forEachIndexed { index, _key -> this[_key] = value[index] }

    operator fun plusAssign(value: Font) = reinitFont(value)

    operator fun plusAssign(value: List<Font>) = value.forEach { reinitFont(it) }

    operator fun plusAssign(value: Array<Font>) = value.forEach { reinitFont(it) }

    operator fun plusAssign(value: Array<Any>) {
        val oversample = (settings["oversample"] as IntSetting?)?.value ?: 1

        if(value.size == 3) {
            val fontFile = File(value[1] as String)

            if(fontFile.isFile)
                genFont(value[0] as String, fontFile, value[2] as Float, oversample)
        }
    }

    operator fun minusAssign(value: Font) = clearFont(value)

    operator fun minusAssign(value: String) {
        val texture = fonts[value]

        if(texture != null)
            clearFont(texture)
    }

    operator fun minusAssign(value: List<Any>) = getFonts(value).forEach { clearFont(it) }

    operator fun minusAssign(value: Array<Any>) = getFonts(value).forEach { clearFont(it) }

    operator fun remAssign(value: Font) = fonts.filterNot { it.value.id == value.id }
                                               .forEach { clearFont(it.value) }
    operator fun remAssign(value: String) {
        if(fonts.containsKey(value))
            fonts.filterNot { it.key == value }
                    .forEach { clearFont(it.value) }
    }

    operator fun remAssign(value: List<Any>) {
        val list = getFonts(value)
        fonts.filterNot { list.any { it2 -> it2.id == it.value.id } }
                .forEach { clearFont(it.value) }
    }

    operator fun remAssign(value: Array<Any>) {
        val list = getFonts(value)
        fonts.filterNot { list.any { it2 -> it2.id == it.value.id } }
                .forEach { clearFont(it.value) }
    }

    fun getFonts(list: List<Any>) : List<Font> {
        return list.filter {
            if(it is Font)
                true
            else
                fonts.containsKey(it)
        }.map { it as? Font ?: fonts[it]!! }
    }

    fun getFonts(list: Array<Any>) : List<Font> {
        return list.filter {
            if(it is Font)
                true
            else
                fonts.containsKey(it)
        }.map { it as? Font ?: fonts[it]!! }
    }

    @JvmOverloads
    fun genFont(name: String, fontFile: File, pointFont: Float, oversample: Int = 1): Font? {
        val width = pow(2.0, ceil(log(2.0, pointFont * 8.0))).roundToInt()
        val height = pow(2.0, ceil(log(2.0, pointFont * 12.0))).roundToInt()
        return genFont(name, fontFile, pointFont, width, height)
    }

    @JvmOverloads
    fun genFont(name: String, fontFile: File, pointFont: Float, width: Int, height: Int, oversample: Int = 1): Font? {
        val data = ioResourceToByteBuffer(fontFile) ?: return null

        val info = STBTTFontinfo.create()

        if(!stbtt_InitFont(info, data)) {
            info.free()
            System.err.println("Could not load font!")
            System.err.println(fontFile)
            return null
        }

        val bitmap = RPmalloc.rpmalloc(width.toLong() * height.toLong())

        if(bitmap == null) {
            info.free()
            System.err.println("Could not create Bitmap for font!")
            System.err.println(fontFile)
            return null
        }

        val font = Font(name, info, width, height, oversample, bitmap)

        STBTTPackContext.malloc().use {
            stbtt_PackBegin(it, bitmap, width, height, 0, 1)

            stbtt_PackSetOversampling(it, oversample, oversample)
            stbtt_PackFontRange(it, data, 0, pointFont, 32, font.chardata)

            stbtt_PackEnd(it)
        }

        genFont(font)

        return font
    }

    fun genFont(font: Font) {
        font.bind()

        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, font.width, font.height, 0, GL_ALPHA, GL_UNSIGNED_BYTE, font.bitmap)

        this[font.name] = font
    }

    fun reinitFont(font: Font) {
        font.destroy()
        font.id = GL11.glGenTextures()

        genFont(font)
    }

    fun clear() {
        fonts.forEach { it.value.close() }
        fonts.clear()
    }

    fun clearFont(font: Font) {
        font.close()
        fonts.remove(font.name)
    }

}