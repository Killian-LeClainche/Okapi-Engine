package polaris.okapi.tests.teamdefense.world

import org.joml.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER
import polaris.okapi.App
import polaris.okapi.options.DoubleSetting
import polaris.okapi.options.IntSetting
import polaris.okapi.options.Key
import polaris.okapi.render.Texture
import polaris.okapi.tests.teamdefense.world.terrain.Grasslands
import polaris.okapi.tests.teamdefense.world.terrain.Ores
import polaris.okapi.tests.teamdefense.world.terrain.PIXELS_PER_TILE
import polaris.okapi.tests.teamdefense.world.terrain.Trees
import polaris.okapi.util.*
import polaris.okapi.world.World
import kotlin.math.*

/**
 * Created by Killian Le Clainche on 4/4/2018.
 */

const val MIN_ZOOM : Double = 0.15
const val MAX_ZOOM : Double = 2.0

class TTDWorld(application: App, val size : Int, seed : Int) : World(application) {

    val worldMap : Array<Array<Tile>> = Array(size, { Array(size, { Tile() }) })

    var center : Vector2d = Vector2d(0.0, 0.0)

    var zoom : Double = 1.0

    val worldMatrix : Matrix4d = Matrix4d().rotateZ(Math.toRadians(-45.0)).rotateY(Math.toRadians(-45.0))
    val entityMatrix : Matrix4d = Matrix4d().rotateZ(Math.toRadians(-45.0)).rotateY(Math.toRadians(180.0))

    val projection : Quad = Quad(48.0 / zoom, (48.0 * .5625) / zoom)

    init {

        val bitmap = Grasslands(size, seed).generate(worldMap)

        if(bitmap != null) {
            textureManager.genTexture("world", size * PIXELS_PER_TILE, size * PIXELS_PER_TILE, 3, bitmap, (settings["mipmap"] as IntSetting?)?.value ?: 0)

            Texture.enable()

            this["world"].bind()
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
        }

        val transitionVector = Vector4d(0.0, 0.0, 0.0, 1.0)

        worldMap.forEach {
            it.forEach {
                it.trees.forEach {
                    transitionVector.set(it.position.x, it.position.y, 0.0, 1.0)

                    entityMatrix.transform(transitionVector)

                    it.position.x = transitionVector.x
                    it.position.y = transitionVector.y
                }
            }
        }

        Trees.values().forEach {
            this[it.name] = "resources/teamdefense/${it.resourceLocation}"
        }

        Ores.values().forEach {
            this[it.name] = "resources/teamdefense/${it.resourceLocation}"
        }
    }

    override fun update() {
        super.update()

        if(settings.scrollDelta.y < 0)
            zoom = max(MIN_ZOOM, zoom - .1)
        else if(settings.scrollDelta.y > 0)
            zoom = min(MAX_ZOOM, zoom + .1)

        val speed = (settings["scroll-speed"] as DoubleSetting).value / zoom

        worldMatrix.translate(-center.x, -center.y, 0.0)

        val movement = Vector2d(0.0, 0.0)

        if((settings["action:scroll-up"] as Key).isPressed)
            movement.y = -speed

        if((settings["action:scroll-down"] as Key).isPressed)
            movement.y = speed

        if((settings["action:scroll-left"] as Key).isPressed)
            movement.x = -speed

        if((settings["action:scroll-right"] as Key).isPressed)
            movement.x = speed

        if(Math.abs(movement.x) + Math.abs(movement.y) > speed) {
            //sqrt(2) / 2
            movement.y *= 0.7071067
        }

        //center.x += movement.x
        //center.y += movement.y

        //System.out.println("${center.x} ${center.y}")

        val diagonal = Math.sqrt(2.0 * size * size)

        center.y = clamp(Math.abs(center.x) * .7071067, diagonal - Math.abs(center.x) * .7071067, center.y + movement.y)

        val maxX = (diagonal / 2 - Math.abs(center.y - diagonal / 2)) / .7071067

        center.x = clamp(-maxX, maxX, center.x + movement.x)

        worldMatrix.translate(center.x, center.y, 0.0)

        projection.set(Quad(48.0 / zoom, (48.0 * .5625) / zoom)).transform(worldMatrix)

        settings.scrollDelta.y = 0.0

    }

    override fun render(delta: Double) {

        Texture.enable()

        this["world"].bind()

        /*GL11.glScaled(zoom, zoom, 1.0)
        GL11.glRotated(30.0, 0.0, 0.0, 1.0)*/

        GL11.glColor4f(1f, 1f, 1f, 1f)
        coordTextureRender()

        GL11.glEnable(GL11.GL_BLEND)
        glEnable(GL_DEPTH_TEST)

        this[Trees.GRASSLAND_TREE_1.name].bind()
        glColor4d(1.0, 1.0, 1.0, 1.0)
        glBegin(GL_QUADS)

        val position = Vector2d(1.0)
        val toSize = size - 1
        val xRange = clamp(0.0, toSize.toDouble(), floor(projection.topRight.y)).toInt() .. clamp(0.0, toSize.toDouble(), ceil(projection.bottomLeft.y)).toInt()
        val yRange = clamp(0.0, toSize.toDouble(), floor(projection.topLeft.x)).toInt() .. clamp(0.0, toSize.toDouble(), ceil(projection.bottomRight.x)).toInt()

        for(i in xRange) {
            for(j in yRange) {
                worldMap[i][j].trees.forEach {

                    //1.414 = sqrt(2)
                    //56.25 = DISPLAY_HEIGHT(1080) / DISPLAY_WIDTH(1920) * 100
                    //.7071067 = sqrt(2) / 2

                    position.x = (it.position.x - center.x / 1.41421356237) * 56.25 * zoom + 960
                    position.y = (it.position.y - center.y) * 56.25 * zoom * .7071067 + 540

                    if (it.treeType == Trees.GRASSLAND_TREE_1 && position.x >= -32 * zoom && position.y <= 1080 + 61 * zoom && position.x <= 1920 + 32 * zoom && position.y >= -61 * zoom) {
                        glTexCoord2d(0.0, 0.0)
                        glVertex3d(position.x - 59.5 * zoom, position.y - 143 * zoom, 0.0)
                        glTexCoord2d(1.0, 0.0)
                        glVertex3d(position.x + 59.5 * zoom, position.y - 143 * zoom, 0.0)
                        glTexCoord2d(1.0, 1.0)
                        glVertex3d(position.x + 59.5 * zoom, position.y, 0.0)
                        glTexCoord2d(0.0, 1.0)
                        glVertex3d(position.x - 59.5 * zoom, position.y, 0.0)
                    }
                }
            }
        }
        glEnd()

        /*this[TreeType.GRASSLAND_TREE_2.name].bind()
        glBegin(GL_QUADS)

        for(i in xRange) {
            for(j in yRange) {
                worldMap[i][j].trees.forEach {

                    //1.414 = sqrt(2)
                    //56.25 = DISPLAY_HEIGHT(1080) / DISPLAY_WIDTH(1920) * 100
                    //.7071067 = sqrt(2) / 2

                    position.x = (it.position.x - center.x / 1.41421356237) * 56.25 * zoom + 960
                    position.y = (it.position.y - center.y) * 56.25 * zoom * .7071067 + 540

                    if (it.treeType == TreeType.GRASSLAND_TREE_2 && position.x >= -32 * zoom && position.y <= 1080 + 61 * zoom && position.x <= 1920 + 32 * zoom && position.y >= -61 * zoom) {
                        glTexCoord2d(0.0, 0.0)
                        glVertex3d(position.x - 32 * zoom, position.y - 61 * zoom, 0.0)
                        glTexCoord2d(1.0, 0.0)
                        glVertex3d(position.x + 32 * zoom, position.y - 61 * zoom, 0.0)
                        glTexCoord2d(1.0, 1.0)
                        glVertex3d(position.x + 32 * zoom, position.y + 3 * zoom, 0.0)
                        glTexCoord2d(0.0, 1.0)
                        glVertex3d(position.x - 32 * zoom, position.y + 3 * zoom, 0.0)
                    }
                }
            }
        }
        glEnd()

        this[TreeType.GRASSLAND_TREE_3.name].bind()
        glBegin(GL_QUADS)

        for(i in xRange) {
            for(j in yRange) {
                worldMap[i][j].trees.forEach {

                    //1.414 = sqrt(2)
                    //56.25 = DISPLAY_HEIGHT(1080) / DISPLAY_WIDTH(1920) * 100
                    //.7071067 = sqrt(2) / 2

                    position.x = (it.position.x - center.x / 1.41421356237) * 56.25 * zoom + 960
                    position.y = (it.position.y - center.y) * 56.25 * zoom * .7071067 + 540

                    if (it.treeType == TreeType.GRASSLAND_TREE_3 && position.x >= -32 * zoom && position.y <= 1080 + 61 * zoom && position.x <= 1920 + 32 * zoom && position.y >= -61 * zoom) {
                        glTexCoord2d(0.0, 0.0)
                        glVertex3d(position.x - 32 * zoom, position.y - 61 * zoom, 0.0)
                        glTexCoord2d(1.0, 0.0)
                        glVertex3d(position.x + 32 * zoom, position.y - 61 * zoom, 0.0)
                        glTexCoord2d(1.0, 1.0)
                        glVertex3d(position.x + 32 * zoom, position.y + 3 * zoom, 0.0)
                        glTexCoord2d(0.0, 1.0)
                        glVertex3d(position.x - 32 * zoom, position.y + 3 * zoom, 0.0)
                    }
                }
            }
        }
        glEnd()*/

        /*this[OreType.ROCKS.name].bind()
        glBegin(GL_QUADS)
        for(i in xRange) {
            for(j in yRange) {
                val tile = worldMap[i][j].tileentity
                if (tile != null && projection.contains(tile.position.x, tile.position.y)) {

                    position.set(tile.position.x, tile.position.y, 0.0, 1.0)

                    Matrix4d().rotateZ(Math.toRadians(45.0)).transform(position)

                    //1.414 = sqrt(2)
                    //56.25 = DISPLAY_HEIGHT(1080) / DISPLAY_WIDTH(1920) * 100
                    //.7071067 = sqrt(2) / 2
                    position.x = (position.x - center.x / 1.41421356237) * 56.25 * zoom + 960
                    position.y = (position.y - center.y) * 56.25 * zoom * .7071067 + 540

                    glTexCoord2d(0.0, 0.0)
                    glVertex3d(position.x, position.y, 0.0)
                    glTexCoord2d(1.0, 0.0)
                    glVertex3d(position.x + 64 * zoom, position.y, 0.0)
                    glTexCoord2d(1.0, 1.0)
                    glVertex3d(position.x + 64 * zoom, position.y + 64 * zoom, 0.0)
                    glTexCoord2d(0.0, 1.0)
                    glVertex3d(position.x, position.y + 64 * zoom, 0.0)
                }
            }
        }*/
        glEnd()

    }

    private fun coordTextureRender() {

        glBegin(GL_QUADS)
        glTexCoord2d(projection.topLeft.x / size, projection.topLeft.y / size)
        glVertex3f(0f, 0f, 0f)
        glTexCoord2d(projection.topRight.x / size, projection.topRight.y / size)
        glVertex3f(1920f, 0f, 0f)
        glTexCoord2d(projection.bottomRight.x / size, projection.bottomRight.y / size)
        glVertex3f(1920f, 1080f, 0f)
        glTexCoord2d(projection.bottomLeft.x / size, projection.bottomLeft.y / size)
        glVertex3f(0f, 1080f, 0f)
        glEnd()

        /*val center = Vector2d(centerX / size, centerY / size)
        val offset = Vector2d(24 / zoom / size, (24 * .5625) / zoom / size)

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2d(center.x + rotateCoordX(-offset.x, -offset.y), center.y + rotateCoordY(-offset.x, -offset.y))
        GL11.glVertex3f(0f, 0f, 0f)
        GL11.glTexCoord2d(center.x + rotateCoordX(offset.x, -offset.y), center.y + rotateCoordY(offset.x, -offset.y))
        GL11.glVertex3f(1920f, 0f, 0f)
        GL11.glTexCoord2d(center.x + rotateCoordX(offset.x, offset.y), center.y + rotateCoordY(offset.x, offset.y))
        GL11.glVertex3f(1920f, 1080f, 0f)
        GL11.glTexCoord2d(center.x + rotateCoordX(-offset.x, offset.y), center.y + rotateCoordY(-offset.x, offset.y))
        GL11.glVertex3f(0f, 1080f, 0f)
        GL11.glEnd()*/
    }

}