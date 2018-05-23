package polaris.okapi.tests.teamdefense.world.terrain

import org.joml.Vector2d
import org.lwjgl.system.rpmalloc.RPmalloc
import polaris.okapi.tests.teamdefense.world.Tile
import polaris.okapi.tests.teamdefense.world.Tree
import polaris.okapi.util.FastNoise
import polaris.okapi.util.*
import java.lang.Math.abs
import java.nio.ByteBuffer

/**
 * Created by Killian Le Clainche on 4/25/2018.
 */

class Grasslands(size : Int, seed : Int) : Generator(size, seed) {

    override fun generate(worldMap: Array<Array<Tile>>) : ByteBuffer? {
        val bitmap = size * PIXELS_PER_TILE;
        val terrain = RPmalloc.rpmalloc((bitmap * bitmap * 3).toLong())

        if(terrain != null) {

            val heatNoise = FastNoise(seed)
            val terrainNoise = FastNoise(seed + 1)
            val heightNoise = FastNoise(seed + 2)
            val shaderNoise = FastNoise(seed + 3)

            heatNoise.frequency = 0.001f
            heatNoise.noiseType = FastNoise.NoiseType.SimplexFractal

            terrainNoise.noiseType = FastNoise.NoiseType.SimplexFractal

            terrainNoise.fractalType = FastNoise.FractalType.FBM

            terrainNoise.octaves = 6
            terrainNoise.gain = .75f

            heightNoise.frequency = .01f
            heightNoise.noiseType = FastNoise.NoiseType.ValueFractal
            heightNoise.octaves = 6
            heightNoise.gain = .4f

            shaderNoise.frequency = .5f
            shaderNoise.noiseType = FastNoise.NoiseType.SimplexFractal
            shaderNoise.fractalType = FastNoise.FractalType.FBM

            shaderNoise.octaves = 6
            shaderNoise.gain = 1f

            var x = 0f
            var y = 0f

            var heatValue = 0f
            var terrainValue = 0f
            var heightValue = 0f
            var shaderValue = 0f

            var red = 0
            var green = 0
            var blue = 0

            for (i in 0 until bitmap) {
                for (j in 0 until bitmap) {

                    x = i / PIXELS_PER_TILE.toFloat()
                    y = j / PIXELS_PER_TILE.toFloat()

                    heatValue = (heatNoise.getNoise(x, y) + 1)
                    terrainValue = terrainNoise.getNoise(x, y) + 1
                    heightValue = heightNoise.getNoise(x, y)
                    shaderValue = shaderNoise.getNoise(x, y)

                    if(heightValue < -.25) {
                        heightValue = 1.0f//clamp(0.0, 1.0, (abs(heightValue) - .25) / .03).toFloat()
                        red = clamp(0, 255, (Terrain.DESERT.red * (1 - heightValue) + (Terrain.SEA.red) * heightValue).toInt())
                        green = clamp(0, 255, (Terrain.DESERT.green * (1 - heightValue) + (Terrain.SEA.green) * heightValue).toInt())
                        blue = clamp(0, 255, (Terrain.DESERT.blue * (1 - heightValue) + (Terrain.SEA.blue) * heightValue).toInt())
                    }
                    else {
                        if(heightValue < -.23) {
                            red = Terrain.GRASSLANDS.red
                            green = Terrain.GRASSLANDS.green
                            blue = Terrain.GRASSLANDS.blue

                            if(shaderValue < (abs(heightValue) - .24) / .01) {
                                red = Terrain.DESERT.red
                                green = Terrain.DESERT.green
                                blue = Terrain.DESERT.blue
                            }
                        }
                        else {
                            red = Terrain.GRASSLANDS.red
                            green = Terrain.GRASSLANDS.green
                            blue = Terrain.GRASSLANDS.blue
                        }
                    }

                    red = clamp(0, 255, red + random(-red * .1, red * .1).toInt())
                    green = clamp(0, 255, green + random(-green * .1, green * .1).toInt())
                    blue = clamp(0, 255, blue + random(-blue * .1, blue * .1).toInt())







                    /*var offset = 1 + simplexNoise.getNoise(i.toFloat() / PIXELS_PER_TILE.toFloat(), j.toFloat() / PIXELS_PER_TILE.toFloat()) / 3.0

                    var red = (clamp(0, 255, Terrain.GRASSLANDS.red)).toByte()
                    var green = (clamp(0, 255, Terrain.GRASSLANDS.green)).toByte()
                    var blue = (clamp(0, 255, Terrain.GRASSLANDS.blue)).toByte()

                    offset = waterNoise.getNoise(i.toFloat() / PIXELS_PER_TILE.toFloat(), j.toFloat() / PIXELS_PER_TILE.toFloat()).toDouble()

                    //System.out.println(offset)
                    if (offset < -.28) {
                        offset = 1 - min(1.0, (abs(offset) - .28) / .3)
                        //offset = 1 - getExpDistance(0.15, offset, .35)
                        //System.out.println(offset)
                        red = (clamp(0.0, 255.0, (red - 0.0) * offset)).toByte()
                        green = (clamp(0.0, 255.0, Terrain.SEA.green + (green - Terrain.SEA.green) * offset)).toByte()
                        blue = (clamp(0.0, 255.0, Terrain.SEA.blue + (blue - Terrain.SEA.blue) * offset)).toByte()

                        red = clamp(0.0, 255.0, red + random(-10.0 * offset, 10.0 * offset)).toByte()
                        green = clamp(0.0, 255.0, green + random(-10.0 * offset, 10.0 * offset)).toByte()
                        blue = clamp(0.0, 255.0, blue + random(-10.0 * offset, 10.0 * offset)).toByte()

                        if (offset <= .7)
                            worldMap[i / PIXELS_PER_TILE][j / PIXELS_PER_TILE].water += offset
                    } else {
                        red = clamp(0.0, 255.0, red + random(-10.0, 10.0)).toByte()
                        green = clamp(0.0, 255.0, green + random(-10.0, 10.0)).toByte()
                        blue = clamp(0.0, 255.0, blue + random(-10.0, 10.0)).toByte()
                    }*/

                    terrain.put(red.toByte())
                    terrain.put(green.toByte())
                    terrain.put(blue.toByte())
                }
            }

            terrain.flip()

            worldMap.forEach {
                it.forEach {
                    it.water /= PIXELS_PER_TILE * PIXELS_PER_TILE.toDouble()
                }
            }

            /*for(i in 0 until size * 2) {
                for(j in 0 until size * 2) {
                    val spot = terrainNoise.getNoise(i.toFloat(), j.toFloat())
                    val tile = worldMap[i / 2][j / 2]
                    if(spot < -.2 && tile.water <= .05) {
                        val treeVector = Vector2d(i / 2.0 + random(.5), j / 2.0 + random(.5))
                        if(random(2) == 0)
                            tile.trees.add(Tree(Trees.GRASSLAND_TREE_1, Vector2d(treeVector.x, treeVector.y)))
                        else if(random(1) == 0)
                            tile.trees.add(Tree(Trees.GRASSLAND_TREE_2, Vector2d(treeVector.x, treeVector.y)))
                        else
                            tile.trees.add(Tree(Trees.GRASSLAND_TREE_3, Vector2d(treeVector.x, treeVector.y)))
                    }
                }
            }*/
        }
        else
            System.err.println("Could not generate terrain!")

        return terrain
    }

}