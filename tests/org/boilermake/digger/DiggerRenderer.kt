package org.boilermake.digger

import org.lwjgl.opengl.GL20C
import org.lwjgl.opengl.GL33C
import polaris.okapi.render.DrawArray
import polaris.okapi.render.VertexAttributes

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */


class DiggerRenderer(private val world: DiggerWorld) {

    lateinit var quad: DrawArray

    var timeExisted: Double = 0.0

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

        quad = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_DYNAMIC_DRAW, quadArray, 6, VertexAttributes.POS_COLOR)
    }

    fun render(delta: Double) {
        timeExisted += delta
        world.application.updateView()
        world.application.renderManager.posColorShader.bind()

        VertexAttributes.POS_COLOR.enable()

        quad.bind()

        val pos = world.playerList[0].position;

        quad.array = floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - 30f, pos.y - 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x + 30f, pos.y - 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x + 30f, pos.y + 30f, 0f,       1f, 0f, 0f, 1f,

                pos.x + 30f, pos.y + 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x - 30f, pos.y - 30f, 0f,       1f, 0f, 0f, 1f,
                pos.x - 30f, pos.y + 30f, 0f,       1f, 0f, 0f, 1f
        )

        quad.draw()

        quad.disable()
    }

}