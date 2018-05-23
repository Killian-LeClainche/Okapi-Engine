package polaris.okapi.tests.teamdefense.world.terrain

import polaris.okapi.tests.teamdefense.world.Tile
import java.nio.ByteBuffer

/**
 * Created by Killian Le Clainche on 4/25/2018.
 */

const val PIXELS_PER_TILE : Int = 16

abstract class Generator(val size : Int, val seed : Int) {

    abstract fun generate(worldMap : Array<Array<Tile>>) : ByteBuffer?

}