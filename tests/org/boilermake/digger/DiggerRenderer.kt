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

    var timeExisted: Double = 0.0

    val playerRenders: MutableList<PlayerRender> = ArrayList()

    fun init() {

        for(i in world.playerList) {
            playerRenders.add(PlayerRender(i))
        }

        world["player"] = "resources/digger/character.png"
    }

    fun render(delta: Double) {

        val texture: Texture = world["player"]

        timeExisted += delta
        world.application.updateView()
        world.application.renderManager.posColorTextureShader.bind()

        VertexAttributes.POS_COLOR_TEXTURE.enable()

        Texture.enable()

        texture.bind()

        for(i in playerRenders) {
            i.render()
        }
    }

}

class PlayerRender(val player: Player) {

    var quad: DrawArray = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_DYNAMIC_DRAW, getQuadArray(), 6, VertexAttributes.POS_COLOR_TEXTURE)

    fun getQuadArray(): FloatArray {
        val pos = player.position;
        return floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     0.01f, .99f,
                pos.x + 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     .99f, .99f,
                pos.x + 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     .99f, 0.01f,

                pos.x + 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     .99f, 0.01f,
                pos.x - 60f, pos.y - 60f, 0f,       1f, 1f, 1f, 1f,     0.01f, .99f,
                pos.x - 60f, pos.y + 60f, 0f,       1f, 1f, 1f, 1f,     0.01f, 0.01f
        )
    }

    fun render() {
        quad.bind()

        quad.array = getQuadArray()

        quad.draw()
    }

}