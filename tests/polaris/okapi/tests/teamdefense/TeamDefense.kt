package polaris.okapi.tests.teamdefense

import org.lwjgl.glfw.GLFW
import polaris.okapi.App
import polaris.okapi.options.*
import polaris.okapi.tests.starship.gui.UIScreen
import polaris.okapi.tests.teamdefense.gui.TTDUI
import polaris.okapi.tests.teamdefense.world.TTDWorld

/**
 * Created by Killian Le Clainche on 4/4/2018.
 */

fun main(args: Array<String>) {
    val app = TeamDefense()

    app.init()

    app.run()
}

class TeamDefense : App(true, true) {

    override fun init(): Boolean {
        if(super.init()) {
            currentWorld = TTDWorld(this)
            initGui(TTDUI(this, (currentWorld as TTDWorld)))
            return true
        }
        return false
    }

    override fun loadSettings(): Settings {
        val settings = Settings()

        //render settings
        settings["mipmap"] = IntSetting(0)
        settings["oversample"] = IntSetting(1)

        //game icon
        settings["icon"] = StringSetting("resources/starship/ships/purple1.png")

        //actions
        //map movement controls
        settings["action:scroll-left"] = Key(GLFW.GLFW_KEY_A)
        settings["action:scroll-right"] = Key(GLFW.GLFW_KEY_D)
        settings["action:scroll-up"] = Key(GLFW.GLFW_KEY_W)
        settings["action:scroll-down"] = Key(GLFW.GLFW_KEY_S)

        //worker management
        settings["action:closest-worker"] = Key(GLFW.GLFW_KEY_F1)
        settings["action:next-worker"] = Key(GLFW.GLFW_KEY_F2)

        //unit management
        settings["action:highlight-unit"] = Key(GLFW.GLFW_MOUSE_BUTTON_1)
        settings["action:highlight-units"] = Key(GLFW.GLFW_MOUSE_BUTTON_1)

        //movement of units / workers
        settings["action:move"] = Key(GLFW.GLFW_MOUSE_BUTTON_1)

        //menus
        settings["action:trade-menu"] = Key(GLFW.GLFW_KEY_T)
        settings["action:economy-menu"] = Key(GLFW.GLFW_KEY_E)
        settings["action:menu"] = Key(GLFW.GLFW_KEY_ESCAPE)
        settings["action:click-menu"] = Key(GLFW.GLFW_MOUSE_BUTTON_1)

        //control groups
        settings["action:grouper"] = Key(GLFW.GLFW_KEY_LEFT_CONTROL)

        for (i in 1 .. 10) {
            settings["action:group-$i"] = Key(GLFW.GLFW_KEY_1)
        }

        //task management
        settings["action:task-queue"] = Key(GLFW.GLFW_KEY_LEFT_SHIFT)

        //OPTIONAL, IN MOST CASES IT'S BEST TO LET DEFAULT BEHAVIOR PERSIST TO HAVE SAVED STATES
        settings.windowMode = WindowMode.WINDOWED

        settings.title = "Team Tower Defense"

        return settings
    }
}