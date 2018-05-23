package polaris.okapi.tests.teamdefense.world

import polaris.okapi.tests.teamdefense.world.terrain.Trees

/**
 * Created by Killian Le Clainche on 4/10/2018.
 */


class Tile {

    val trees: MutableList<Tree> = ArrayList()
    var tileentity : Ore? = null
    var water: Double = 0.0

    fun renderTreeSet(type: Trees) {

    }

}