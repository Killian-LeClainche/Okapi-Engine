package org.boilermake.digger

import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20C
import org.lwjgl.opengl.GL30C
import org.lwjgl.opengl.GL33C
import polaris.okapi.render.DrawArray
import polaris.okapi.render.Shader
import polaris.okapi.render.Texture
import polaris.okapi.render.VertexAttributes
import polaris.okapi.util.isEqual
import polaris.okapi.util.random
import java.awt.Color.gray
import java.io.File
import java.lang.Math.abs

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */


class DiggerRenderer(private val world: DiggerWorld) {

    var timeExisted: Double = 0.0

    val playerRenders: MutableList<PlayerRender> = ArrayList()
    val terrainRenders: MutableList<TerrainRender> = ArrayList()
    val graveRenders: MutableList<BlockRender> = ArrayList()

    val rainShader: Shader = Shader()
    val rainQuad: DrawArray = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_STATIC_DRAW, floatArrayOf(
            0f, 0f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            1920f, 0f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            1920f, 1080f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,

            1920f, 1080f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            0f, 0f, 0f,     0f, 0f, 0f, 0f,     0f, 0f,
            0f, 1080f, 0f,     0f, 0f, 0f, 0f,     0f, 0f
    ), 6, VertexAttributes.POS_COLOR_TEXTURE)

    val blockFogShader: Shader = Shader()

    fun init() {

        for(i in world.playerList) {
            if (!i.isDead) {
                playerRenders.add(PlayerRender(world, i))

            }
        }

        for(i in world.terrainList) {
            terrainRenders.add(TerrainRender(this, i))
        }

        for(i in world.graveList) {
            if(!i.isDug) {
                graveRenders.add(BlockRender(i))
            }
        }

        rainShader.vertexShaderId = world.application.renderManager.loadShader(File("resources/digger/shaders/rain.vert"), GL20C.GL_VERTEX_SHADER)
        rainShader.fragmentShaderId = world.application.renderManager.loadShader(File("resources/digger/shaders/rain.frag"), GL20C.GL_FRAGMENT_SHADER)

        rainShader.link()

        blockFogShader.vertexShaderId = world.application.renderManager.loadShader(File("resources/digger/shaders/fog.vert"), GL20C.GL_VERTEX_SHADER)
        blockFogShader.fragmentShaderId = world.application.renderManager.loadShader(File("resources/digger/shaders/fog.frag"), GL20C.GL_FRAGMENT_SHADER)

        blockFogShader.link()

        world["player"] = "resources/digger/character.png"
        world["ground"] = "resources/digger/ground.png"
        world["dead"] = "resources/digger/characterdead.png"

        world["idle-animation"] = "resources/digger/IdleAnimation.png"
        world["idle-animation:claymore"] = "resources/digger/IdleClaymoreAnimation.png"
        world["idle-animation:sword"] = "resources/digger/IdleSwordAnimation.png"
        world["idle-animation:godfist"] = "resources/digger/IdleGodFistAnimation.png"
        world["idle-animation:gun"] = "resources/digger/IdleGunAnimation.png"

        world["run-animation"] = "resources/digger/RunAnimation.png"
        world["run-animation:claymore"] = "resources/digger/RunClaymoreAnimation.png"
        world["run-animation:sword"] = "resources/digger/RunSwordAnimation.png"
        world["run-animation:godfist"] = "resources/digger/RunGodFistAnimation.png"
        world["run-animation:gun"] = "resources/digger/RunGunAnimation.png"

        world["jump-animation"] = "resources/digger/JumpAnimation.png"
        world["fall-animation"] = "resources/digger/FallAnimation.png"
        world["dig-animation"] = "resources/digger/DigAnimation.png"
        world["double-jump-animation"] = "resources/digger/DoubleJumpAnimation.png"

        world["attack-animation:claymore"] = "resources/digger/AttackClaymoreAnimation.png"
        world["attack-animation:halberd"] = "resources/digger/AttackHalberdAnimation.png"
        world["attack-animation:sword"] = "resources/digger/AttackSwordAnimation.png"

        world["idle-claymore"] = "resources/digger/IdleClaymore.png"
        world["idle-halberd"] = "resources/digger/IdleHalberd.png"
        world["idle-sword"] = "resources/digger/IdleSword.png"
        world["idle-godfist"] = "resources/digger/IdleGodFistItem.png"
        world["idle-rifle"] = "resources/digger/IdleRifleItem.png"
        world["idle-shotgun"] = "resources/digger/IdleShotgunItem.png"
        world["idle-sniper"] = "resources/digger/IdleSniperItem.png"

        world["run-claymore"] = "resources/digger/RunClaymoreItem.png"
        world["run-halberd"] = "resources/digger/RunHalberdItem.png"
        world["run-sword"] = "resources/digger/RunSwordItem.png"
        world["run-godfist"] = "resources/digger/RunGodFistItem.png"
        world["run-rifle"] = "resources/digger/RunRifleItem.png"
        world["run-shotgun"] = "resources/digger/RunShotgunItem.png"
        world["run-sniper"] = "resources/digger/RunSniperItem.png"

        world["attack-claymore"] = "resources/digger/AttackClaymoreItem.png"
        world["attack-halberd"] = "resources/digger/AttackHalberdItem.png"
        world["attack-sword"] = "resources/digger/AttackSwordItem.png"
    }

    fun render(delta: Double) {

        val texture: Texture = world["player"]

        GL30C.glEnable(GL30C.GL_BLEND)
        GL30C.glBlendFunc(GL30C.GL_SRC_ALPHA, GL30C.GL_ONE_MINUS_SRC_ALPHA)

        timeExisted += delta
        world.application.updateView()
        VertexAttributes.POS_COLOR_TEXTURE.enable()

        rainShader.bind()

        GL30C.glUniform1f(rainShader["time"], (System.currentTimeMillis() % 10000000) / 1000f)
        GL30C.glUniform2f(rainShader["resolution"], world.settings.windowWidth.toFloat(), world.settings.windowHeight.toFloat())
        GL30C.glUniform1f(rainShader["size"], .075f)

        Texture.disable()
        rainQuad.bind()
        rainQuad.draw()

        world.application.renderManager.posColorTextureShader.bind()

        Texture.enable()

        world["ground"].bind()

        var i = 0
        while(i < graveRenders.size) {
            val grave = graveRenders[i]
            if((grave.block as Grave).isDug) {
                graveRenders.removeAt(i)
                i--
            }
            else
                grave.render()
            i++
        }

        texture.bind()

        for(i in playerRenders) {
            i.render()
        }

        blockFogShader.bind()

        GL30C.glUniform1f(blockFogShader["time"], (System.currentTimeMillis() % 10000000) / 1000f)
        GL30C.glUniform2f(blockFogShader["resolution"], world.settings.windowWidth.toFloat(), world.settings.windowHeight.toFloat())

        for(i in terrainRenders) {
            i.render(false)
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

class Animation(val player: Player, var id: Int, var size: Int, var animation: String) {

    var ticks = 0

    fun update() {
        ticks++;

        when {
            animation == "dig-animation" -> {
                if(player.hasClickedGrave()) {
                    id = 2 * (10 - abs(ticks - 10))
                    if(ticks == 20)
                        ticks = 0
                }
                else {
                    ticks = 0
                    id = 0
                }
            }
            animation == "idle-animation:sword" || animation == "idle-animation:godfist" || animation == "idle-godfist" -> {
                if(ticks >= 32) {
                    ticks = 0
                    id = (id + 1) % size
                }
            }
            else -> {
                if(ticks >= 8) {
                    ticks = 0
                    id = (id + 1) % size
                }
            }
        }
    }

    fun swap(newAnimation: String) {
        if(animation != newAnimation) {
            id = 0
            size = 30
            animation = newAnimation
            when (animation) {
                "idle-animation:gun" -> size = 1
                "idle-animation:claymore" -> size = 1
                "idle-animation:sword" -> size = 5
                "idle-claymore" -> size = 1
                "idle-halberd" -> size = 1
                "idle-sword" -> size = 1
                "idle-shotgun" -> size = 1
                "idle-sniper" -> size = 1
                "idle-rifle" -> size = 1
            }
        }
    }
}

class PlayerRender(val world: DiggerWorld, val player: Player) {
    var animation: Animation = Animation(player, 0, 30, "idle-animation")
    var itemAnimation: Animation = Animation(player, 0, 30, "idle-animation")
    val quad: DrawArray = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_DYNAMIC_DRAW, getQuadArray(), 6, VertexAttributes.POS_COLOR_TEXTURE)
    val itemQuad: DrawArray = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_DYNAMIC_DRAW, getItemQuadArray(), 6, VertexAttributes.POS_COLOR_TEXTURE)


    fun getQuadArray(): FloatArray {
        val pos = player.position
        val size = Vector2f(player.size)
        val color = Vector3f(player.color)

        color.x /= 255f
        color.y /= 255f
        color.z /= 255f

        size.x /= 2
        size.y /= 2

        val uSize = 1f / animation.size
        var u1 = uSize * animation.id
        var u2 = u1 + uSize

        if(player.isFacingLeft == (animation.animation == "idle-animation:godfist")) {
            val tempU = u1
            u1 = u2
            u2 = tempU
        }

        return floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - size.x, pos.y - size.y, 0f,       color.x, color.y, color.z, 1f,     u2, 1f,
                pos.x + size.x, pos.y - size.y, 0f,       color.x, color.y, color.z, 1f,     u1, 1f,
                pos.x + size.x, pos.y + size.y, 0f,       color.x, color.y, color.z, 1f,     u1, 0.0f,

                pos.x + size.x, pos.y + size.y, 0f,       color.x, color.y, color.z, 1f,     u1, 0.0f,
                pos.x - size.x, pos.y - size.y, 0f,       color.x, color.y, color.z, 1f,     u2, 1f,
                pos.x - size.x, pos.y + size.y, 0f,       color.x, color.y, color.z, 1f,     u2, 0.0f
        )
    }

    fun getItemQuadArray(): FloatArray {
        val pos = player.position
        val size = Vector2f(player.size)

        val uSize = 1f / itemAnimation.size
        var u1 = uSize * itemAnimation.id
        var u2 = u1 + uSize
        var faceRight = 1

        if(player.isFacingLeft == (animation.animation == "idle-animation:godfist")) {
            faceRight = -1
            val tempU = u1
            u1 = u2
            u2 = tempU
        }

        if(itemAnimation.animation == "idle-claymore")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - 10, pos.y - 10, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + 10, pos.y - 10, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + 10, pos.y + 64, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + 10, pos.y + 64, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - 10, pos.y - 10, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - 10, pos.y + 64, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "run-claymore")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 2, pos.y + 64, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 2, pos.y + 64, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 2, pos.y + 64, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "idle-godfist")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 2, pos.y + size.y / 2 - 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 2, pos.y + size.y / 2 - 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 2, pos.y + size.y / 2 - 16, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "idle-sniper")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x/ 1.5f - 32 * faceRight, pos.y - size.y / 12 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x/ 1.5f - 32 * faceRight, pos.y - size.y / 12 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x/ 1.5f - 32 * faceRight, pos.y + size.y / 12 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x/ 1.5f - 32 * faceRight, pos.y + size.y / 12 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x/ 1.5f - 32 * faceRight, pos.y - size.y / 12 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x/ 1.5f - 32 * faceRight, pos.y + size.y / 12 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "run-sniper")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 1.5f - 32 * faceRight, pos.y - size.y / 6 + 24, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 1.5f - 32 * faceRight, pos.y - size.y / 6 + 24, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 1.5f - 32 * faceRight, pos.y + size.y / 6 + 24, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 1.5f - 32 * faceRight, pos.y + size.y / 6 + 24, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 1.5f - 32 * faceRight, pos.y - size.y / 6 + 24, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 1.5f - 32 * faceRight, pos.y + size.y / 6 + 24, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "idle-sword")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 2 - 4 * faceRight, pos.y - size.y / 2 + 8, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 2 - 4 * faceRight, pos.y - size.y / 2 + 8, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 2 - 4 * faceRight, pos.y + size.y / 2 + 8, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 2 - 4 * faceRight, pos.y + size.y / 2 + 8, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 2 - 4 * faceRight, pos.y - size.y / 2 + 8, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 2 - 4 * faceRight, pos.y + size.y / 2 + 8, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "idle-halberd")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 6 + 18 * faceRight, pos.y - size.y / 3 - 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 6 + 18 * faceRight, pos.y - size.y / 3 - 16, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 6 + 18 * faceRight, pos.y + size.y / 3 - 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 6 + 18 * faceRight, pos.y + size.y / 3 - 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 6 + 18 * faceRight, pos.y - size.y / 3 - 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 6 + 18 * faceRight, pos.y + size.y / 3 - 16, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "run-godfist")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 4 - 32 * faceRight, pos.y - size.y / 4 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 4 - 32 * faceRight, pos.y - size.y / 4 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 4 - 32 * faceRight, pos.y + size.y / 4 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 4 - 32 * faceRight, pos.y + size.y / 4 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 4 - 32 * faceRight, pos.y - size.y / 4 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 4 - 32 * faceRight, pos.y + size.y / 4 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "idle-rifle")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 3 - 16 * faceRight, pos.y - size.y / 9 + 23, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 3 - 16 * faceRight, pos.y - size.y / 9 + 23, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 3 - 16 * faceRight, pos.y + size.y / 9 + 23, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 3 - 16 * faceRight, pos.y + size.y / 9 + 23, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 3 - 16 * faceRight, pos.y - size.y / 9 + 23, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 3 - 16 * faceRight, pos.y + size.y / 9 + 23, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "run-rifle")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 3 - 16 * faceRight, pos.y - size.y / 7 + 23, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 3 - 16 * faceRight, pos.y - size.y / 7 + 23, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 3 - 16 * faceRight, pos.y + size.y / 7 + 23, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 3 - 16 * faceRight, pos.y + size.y / 7 + 23, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 3 - 16 * faceRight, pos.y - size.y / 7 + 23, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 3 - 16 * faceRight, pos.y + size.y / 7 + 23, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "idle-shotgun")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x / 4 - 16 * faceRight, pos.y - size.y / 12 + 20, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x / 4 - 16 * faceRight, pos.y - size.y / 12 + 20, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x / 4 - 16 * faceRight, pos.y + size.y / 12 + 20, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x / 4 - 16 * faceRight, pos.y + size.y / 12 + 20, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x / 4 - 16 * faceRight, pos.y - size.y / 12 + 20, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x / 4 - 16 * faceRight, pos.y + size.y / 12 + 20, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )
        if(itemAnimation.animation == "run-halberd")
            return floatArrayOf(
                    //position          color                   texture
                    //x,y,z             r,g,b,a                 u,v
                    pos.x - size.x, pos.y - size.y / 2 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x + size.x, pos.y - size.y / 2 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                    pos.x + size.x, pos.y + size.y / 2 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                    pos.x + size.x, pos.y + size.y / 2 + 16, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                    pos.x - size.x, pos.y - size.y / 2 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                    pos.x - size.x, pos.y + size.y / 2 + 16, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
            )

        return floatArrayOf(
                //position          color                   texture
                //x,y,z             r,g,b,a                 u,v
                pos.x - size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                pos.x + size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u1, 1f,
                pos.x + size.x / 2, pos.y + size.y / 2, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,

                pos.x + size.x / 2, pos.y + size.y / 2, 0f,       1f, 1f, 1f, 1f,     u1, 0.0f,
                pos.x - size.x / 2, pos.y - size.y / 2, 0f,       1f, 1f, 1f, 1f,     u2, 1f,
                pos.x - size.x / 2, pos.y + size.y / 2, 0f,       1f, 1f, 1f, 1f,     u2, 0.0f
        )
    }

    fun render() {
        when {
            player.isGoingUp -> animation.swap("double-jump-animation")
            player.isFalling -> animation.swap("double-jump-animation")
            player.isDoubleJumping -> animation.swap("double-jump-animation")
            player.isGraveDigging -> animation.swap("dig-animation")
            player.isIdle -> {
                when {
                    player.item == 0 -> animation.swap("idle-animation")
                    player.item == 1 -> animation.swap("idle-animation:sword")
                    player.item == 2 -> animation.swap("idle-animation:claymore")
                    player.item == 3 -> animation.swap("idle-animation:sword")
                    player.item < 7 -> animation.swap("idle-animation:gun")
                    else -> animation.swap("idle-animation:godfist")
                }
            }
            player.isGoingUp -> animation.swap("fall-animation")
            player.isFalling -> animation.swap("fall-animation")
            player.isDoubleJumping -> animation.swap("double-jump-animation")
            player.isGraveDigging -> animation.swap("dig-animation")

            else -> {
                when(player.item) {
                    0 -> animation.swap("run-animation")
                    1 -> animation.swap("run-animation:sword")
                    2 -> animation.swap("run-animation:claymore")
                    3 -> animation.swap("run-animation:sword")
                    4 -> animation.swap("run-animation:gun")
                    5 -> animation.swap("run-animation:gun")
                    6 -> animation.swap("run-animation:gun")
                    else -> animation.swap("run-animation:godfist")
                }
            }
        }

        animation.update()

        world[animation.animation].bind()

        quad.bind()

        quad.array = getQuadArray()

        quad.draw()

        if(player.item > 0 && (animation.animation != "double-jump-animation" && animation.animation !=  "dig-animation")) {
            when {
                animation.animation.startsWith("idle-animation") -> {
                    when(player.item) {
                        1 -> itemAnimation.swap("idle-sword")
                        2 -> itemAnimation.swap("idle-claymore")
                        3 -> itemAnimation.swap("idle-halberd")
                        4 -> itemAnimation.swap("idle-sniper")
                        5 -> itemAnimation.swap("idle-shotgun")
                        6 -> itemAnimation.swap("idle-rifle")
                        else -> itemAnimation.swap("idle-godfist")
                    }
                }
                animation.animation.startsWith("run-animation") -> {
                    when(player.item) {
                        1 -> itemAnimation.swap("run-sword")
                        2 -> itemAnimation.swap("run-claymore")
                        3 -> itemAnimation.swap("run-halberd")
                        4 -> itemAnimation.swap("run-sniper")
                        5 -> itemAnimation.swap("run-shotgun")
                        6 -> itemAnimation.swap("run-rifle")
                        else -> itemAnimation.swap("run-godfist")
                    }
                }
            }

            itemAnimation.update()

            world[itemAnimation.animation].bind()

            itemQuad.bind()

            itemQuad.array = getItemQuadArray()

            itemQuad.draw()
        }
    }
}

class TerrainRender(val renderer: DiggerRenderer, val block: Block) {
    val blockRender: BlockRender = BlockRender(block)
    val quad: DrawArray = DrawArray(GL20C.GL_TRIANGLES, GL20C.GL_STATIC_DRAW, getQuadArray(), 6, VertexAttributes.POS_COLOR_TEXTURE)

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

    fun render(pass: Boolean) {
        val pos = block.position
        val size = Vector2f(block.size)
        GL30C.glUniform2f(renderer.blockFogShader["bottom"], pos.x - size.x / 2, pos.y - size.y / 2)
        GL30C.glUniform2f(renderer.blockFogShader["top"], pos.x + size.x / 2, pos.y + size.y / 2)
        quad.bind()
        quad.draw()
    }
}