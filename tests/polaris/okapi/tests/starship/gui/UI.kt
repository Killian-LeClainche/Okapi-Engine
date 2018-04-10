package polaris.okapi.tests.starship.gui

import polaris.okapi.App
import polaris.okapi.gui.Gui
import polaris.okapi.world.World

/**
 * Created by Killian Le Clainche on 2/23/2018.
 */

class UIScreen @JvmOverloads constructor(application: App, parent: Gui? = null, ticksExisted: Double = 0.0) : Gui(application, parent, ticksExisted) {

    override fun init() {
        super.init()
    }

    override fun render(delta: Double) {
        super.render(delta)
    }

}