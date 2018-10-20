package org.boilermake.digger

import org.lwjgl.opengl.GL20C
import polaris.okapi.render.DrawArray
import polaris.okapi.render.Shader
import polaris.okapi.render.VertexAttributes
import polaris.okapi.world.Vector

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */


class DiggerRenderer(private val world: DiggerWorld) {

    lateinit var quad: DrawArray

    fun init() {

        val pos = world.playerList[0].position;

        val quadArray = floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - 30f, pos.y - 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x + 30f, pos.y - 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x + 30f, pos.y + 30f, 0f,       1f, 0f, 0f, 1f,

                pos.x + 30f, pos.y + 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x - 30f, pos.y - 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x - 30f, pos.y + 30f, 0f,       1f, 0f, 0f, 1f
        )

        quad = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_STATIC_DRAW, quadArray, 6, VertexAttributes.POS_COLOR)
    }

    fun render(delta: Double) {
        world.application.updateView()
        world.application.renderManager.posColorShader.bind()

        VertexAttributes.POS_COLOR.enable()

        quad.bind()

        quad.draw()

        quad.disable()
    }

}