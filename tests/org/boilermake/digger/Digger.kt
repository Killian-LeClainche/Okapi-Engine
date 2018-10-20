package org.boilermake.digger

import org.lwjgl.glfw.GLFW
import polaris.okapi.App
import polaris.okapi.options.Key
import polaris.okapi.options.Settings
import polaris.okapi.options.WindowMode

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */

fun main(args: Array<String>) {
    val app = WindowCreation()

    app.init()

    app.run()
}

fun addCount(count: Int): Int = count + 1

fun addCount(count: Int, function: (Int) -> Int): Int = function(count)

class WindowCreation : App(true) {

    override fun init(): Boolean {
        if(super.init()) {
            currentScreen = DiggerHUD(this)
            currentWorld = DiggerWorld(this)
            return true
        }
        return false
    }

    override fun loadSettings(): Settings {
        val settings = Settings()

        settings["mipmap"] = 0
        settings["oversample"] = 1
        settings["icon"] = "resources/boilermake.png"
        settings["p1:action:right"] = Key(GLFW.GLFW_KEY_D)
        settings["p1:action:up"] = Key(GLFW.GLFW_KEY_W)
        settings["p1:action:left"] = Key(GLFW.GLFW_KEY_A)
        settings["p1:action:down"] = Key(GLFW.GLFW_KEY_S)
        settings["p1:action:jump"] = Key(GLFW.GLFW_KEY_SPACE)

        //OPTIONAL, IN MOST CASES IT'S BEST TO LET DEFAULT BEHAVIOR PERSIST TO HAVE SAVED STATES
        settings.windowMode = WindowMode.WINDOWED

        settings.title = "Window Creation Test"

        return settings
    }
}
