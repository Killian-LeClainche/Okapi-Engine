package org.boilermake.digger

import polaris.okapi.App
import polaris.okapi.options.Settings
import polaris.okapi.options.WindowMode
import polaris.okapi.tests.example1.ExampleGui

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
            currentScreen = DiggerGui(this)
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

        //OPTIONAL, IN MOST CASES IT'S BEST TO LET DEFAULT BEHAVIOR PERSIST TO HAVE SAVED STATES
        settings.windowMode = WindowMode.WINDOWED

        settings.title = "Window Creation Test"

        return settings
    }
}
