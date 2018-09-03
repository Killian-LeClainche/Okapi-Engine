package polaris.okapi.tests.example1

import org.lwjgl.opengl.GL20C.*
import polaris.okapi.App
import polaris.okapi.gui.Gui
import polaris.okapi.render.DrawArray
import polaris.okapi.render.Shader
import polaris.okapi.render.Texture
import polaris.okapi.render.VertexAttributes
import java.io.File


/**
 * Created by Killian Le Clainche on 12/18/2017.
 */


class ExampleGui @JvmOverloads constructor(application: App, parent: Gui? = null, ticksExisted: Double = 0.0) : Gui(application, parent, ticksExisted) {

    lateinit var boilermake: Texture

    val example1Shader: Shader = Shader()
    var timeID: Int = 0
    lateinit var quad: DrawArray

    override fun init() {
        super.init()

        textures["boilermake"] = "resources/starship/ships/purple1.png"
        boilermake = textures["boilermake"]

        example1Shader[GL_VERTEX_SHADER] = "resources/example/shaders/example1.vert"
        example1Shader[GL_FRAGMENT_SHADER] = "resources/example/shaders/example1.frag"

        example1Shader.link()

        timeID = example1Shader["time"]

        val quadArray = floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                -1f, -1f, 0f,       1f, 0f, 0f, 1f,
                1f, -1f, 0f,       1f, 0f, 0f, 1f,
                1f, 1f, 0f,       1f, 0f, 0f, 1f,

                1f, 1f, 0f,       1f, 0f, 0f, 1f,
                -1f, -1f, 0f,       1f, 0f, 0f, 1f,
                -1f, 1f, 0f,       1f, 0f, 0f, 1f
        )

        quad = DrawArray(GL_TRIANGLES, GL_STATIC_DRAW, quadArray, 6, VertexAttributes.POS_COLOR)
    }

    override fun render(delta: Double) {
        super.render(delta)

        example1Shader.bind()

        VertexAttributes.POS_COLOR.enable()

        quad.bind()

        quad.draw()

        quad.disable()

        example1Shader.disable()

    }

    override fun close() {
        quad.destroy()

        textures -= "boilermake"
    }

}
