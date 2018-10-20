package org.boilermake.digger

import org.lwjgl.opengl.GL20C
import org.lwjgl.opengl.GL33C
import polaris.okapi.render.DrawArray
import polaris.okapi.render.Texture
import polaris.okapi.render.VertexAttributes

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */


class DiggerRenderer(private val world: DiggerWorld) {

    lateinit var quad: DrawArray

    var timeExisted: Double = 0.0

    var shaderTexID: Int = 0

    fun init() {

        val pos = world.playerList[0].position;

        val quadArray = floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     0f, 1f,
                pos.x + 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     1f, 1f,
                pos.x + 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     1f, 0f,

                pos.x + 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     1f, 0f,
                pos.x - 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     0f, 1f,
                pos.x - 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     0f, 1f
        )

        quad = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_DYNAMIC_DRAW, quadArray, 6, VertexAttributes.POS_COLOR_TEXTURE)

        world["player"] = "resources/digger/character.png"
    }

    fun render(delta: Double) {

        val texture: Texture = world["player"]

        timeExisted += delta
        world.application.updateView()
        world.application.renderManager.posColorTextureShader.bind()

        VertexAttributes.POS_COLOR_TEXTURE.enable()

        Texture.enable()

        quad.bind()

        val pos = world.playerList[0].position;

        quad.array = floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     0f, 1f,
                pos.x + 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     1f, 1f,
                pos.x + 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     1f, 0f,

                pos.x + 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     1f, 0f,
                pos.x - 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     0f, 1f,
                pos.x - 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     0f, 0f
        )

        quad.draw()

        quad.disable()
    }

}