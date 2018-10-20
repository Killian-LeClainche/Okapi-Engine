package org.boilermake.digger

import org.joml.Vector2f
import org.lwjgl.opengl.GL20C
import org.lwjgl.opengl.GL30C
import org.lwjgl.opengl.GL33C
import polaris.okapi.render.DrawArray
import polaris.okapi.render.Shader
import polaris.okapi.render.Texture
import polaris.okapi.render.VertexAttributes
import java.awt.Color.gray
import java.io.File

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */


class DiggerRenderer(private val world: DiggerWorld) {

    var timeExisted: Double = 0.0

    val playerRenders: MutableList<BlockRender> = ArrayList()
    val terrainRenders: MutableList<BlockRender> = ArrayList()

    val rainShader: Shader = Shader()
    val rainQuad: DrawArray = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_STATIC_DRAW, floatArrayOf(
            0f, 0f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            1920f, 0f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            1920f, 1080f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,

            1920f, 1080f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            0f, 0f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            0f, 1080f, 0f,     0f, 0f, 0f, 0f,     0f, 0f
    ), 6, VertexAttributes.POS_COLOR_TEXTURE)

    fun init() {

        for(i in world.playerList) {
            playerRenders.add(BlockRender(i))
        }

        for(i in world.terrainList) {
            terrainRenders.add(BlockRender(i))
        }

        rainShader.vertexShaderId = world.application.renderManager.loadShader(File("resources/digger/shaders/rain.vert"), GL20C.GL_VERTEX_SHADER)
        rainShader.fragmentShaderId = world.application.renderManager.loadShader(File("resources/digger/shaders/rain.frag"), GL20C.GL_FRAGMENT_SHADER)

        rainShader.link()

        world["player"] = "resources/digger/character.png"
        world["ground"] = "resources/digger/ground.png"

    }

    fun render(delta: Double) {

        val texture: Texture = world["player"]

        timeExisted += delta
        world.application.updateView()

        rainShader.bind()

        GL30C.glUniform1f(rainShader["time"], (System.currentTimeMillis() % 10000000) / 100f)
        GL30C.glUniform2f(rainShader["resolution"], world.settings.windowWidth.toFloat(), world.settings.windowHeight.toFloat())
        GL30C.glUniform1f(rainShader["hue"], .5f)
        GL30C.glUniform1f(rainShader["fade"], 1f)
        GL30C.glUniform1f(rainShader["slow"], 10f)
        GL30C.glUniform1f(rainShader["gray"], .3f)

        VertexAttributes.POS_COLOR_TEXTURE.enable()
        Texture.disable()
        rainQuad.bind()
        rainQuad.draw()

        world.application.renderManager.posColorTextureShader.bind()


        Texture.enable()

        texture.bind()

        for(i in playerRenders) {
            i.render()
        }

        world["ground"].bind()

        for(i in terrainRenders) {
            i.render()
        }
    }

}

class BlockRender(val block: Block) {

    val quad: DrawArray = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_DYNAMIC_DRAW, getQuadArray(), 6, VertexAttributes.POS_COLOR_TEXTURE)

    fun getQuadArray(): FloatArray {
        val pos = block.position
        val size = Vector2f(block.size)

        size.x /= 2
        size.y /= 2
        return floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - size.x, pos.y - size.y, 0f,       1f, 1f, 1f, 1f,     0.01f, .99f,
                pos.x + size.x, pos.y - size.y, 0f,       1f, 1f, 1f, 1f,     .99f, .99f,
                pos.x + size.x, pos.y + size.y, 0f,       1f, 1f, 1f, 1f,     .99f, 0.01f,

                pos.x + size.x, pos.y + size.y, 0f,       1f, 1f, 1f, 1f,     .99f, 0.01f,
                pos.x - size.x, pos.y - size.y, 0f,       1f, 1f, 1f, 1f,     0.01f, .99f,
                pos.x - size.x, pos.y + size.y, 0f,       1f, 1f, 1f, 1f,     0.01f, 0.01f
        )
    }

    fun render() {
        quad.bind()

        quad.array = getQuadArray()

        quad.draw()
    }

}