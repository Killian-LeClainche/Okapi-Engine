package polaris.okapi.tests.starship

import org.lwjgl.glfw.GLFW
import polaris.okapi.App
import polaris.okapi.options.Key
import polaris.okapi.options.Settings
import polaris.okapi.options.WindowMode
import polaris.okapi.tests.starship.gui.UIScreen
import polaris.okapi.tests.starship.world.StarshipWorld

/**
 * Created by Killian Le Clainche on 2/23/2018.
 */

const val UPDATE_TIME_INTERVAL : Double = 1 / 60.0

fun main(args: Array<String>) {
    val app = StarshipFighter()

    app.init()

    app.run()
}

class StarshipFighter : App(true) {

    override fun init(): Boolean {
        if(super.init()) {
            currentScreen = UIScreen(this)
            currentWorld = StarshipWorld(this)
            return true
        }
        return false
    }

    override fun loadSettings(): Settings {
        val settings = Settings()

        settings["mipmap"] = 0
        settings["oversample"] = 1
        settings["icon"] = "resources/starship/ships/purple1.png"
        settings["action:move"] = Key(GLFW.GLFW_MOUSE_BUTTON_1)

        //OPTIONAL, IN MOST CASES IT'S BEST TO LET DEFAULT BEHAVIOR PERSIST TO HAVE SAVED STATES
        settings.windowMode = WindowMode.WINDOWED

        settings.title = "Window Creation Test"

        return settings
    }
}