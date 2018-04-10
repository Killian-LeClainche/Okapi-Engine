package polaris.okapi.tests.teamdefense.gui

import polaris.okapi.App
import polaris.okapi.gui.Gui
import polaris.okapi.tests.teamdefense.world.TTDWorld
import polaris.okapi.world.World

/**
 * Created by Killian Le Clainche on 4/4/2018.
 */

class TTDUI @JvmOverloads constructor(application: App, world: TTDWorld, parent: Gui? = null, ticksExisted: Double = 0.0) : Gui(application, parent, ticksExisted) {

    val minimap : Minimap = Minimap(world)

    override fun init() {
        super.init()

        minimap.init()
    }

    override fun render(delta: Double) {
        super.render(delta)

        minimap.render(delta)
    }

}