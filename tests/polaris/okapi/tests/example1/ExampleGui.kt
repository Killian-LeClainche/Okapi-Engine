package polaris.okapi.tests.example1

import org.lwjgl.bgfx.BGFX.Functions.begin
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import polaris.okapi.App
import polaris.okapi.gui.Gui
import polaris.okapi.render.Texture
import kotlin.math.roundToLong

/**
 * Created by Killian Le Clainche on 12/18/2017.
 */


class ExampleGui @JvmOverloads constructor(application: App, parent: Gui? = null, ticksExisted: Double = 0.0) : Gui(application, parent, ticksExisted) {

    lateinit var boilermake: Texture

    override fun init() {
        super.init()

        textures["boilermake"] = "resources/starship/ships/purple1.png"
        boilermake = textures["boilermake"]


    }

    override fun render(delta: Double) {
        super.render(delta)

        Texture.enable()

        textures["boilermake"].bind()
        GL11.glBegin(GL_QUADS)
        glTexCoord2f(0f, 0f)
        glVertex3f(0f, 0f, 0f)
        glTexCoord2f(1f, 0f)
        glVertex3f(256f, 0f, 0f)
        glTexCoord2f(1f, 1f)
        glVertex3f(256f, 256f, 0f)
        glTexCoord2f(0f, 1f)
        glVertex3f(0f, 256f, 0f)
        glEnd()
    }

}
