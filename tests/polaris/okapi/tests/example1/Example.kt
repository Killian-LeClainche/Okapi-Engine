package polaris.okapi.tests.example1

import polaris.okapi.App
import polaris.okapi.options.IntSetting
import polaris.okapi.options.Settings
import polaris.okapi.options.StringSetting
import polaris.okapi.options.WindowMode

/**
 * Created by Killian Le Clainche on 12/15/2017.
 */

fun main(args: Array<String>) {
    val app = WindowCreation()

    app.init()

    app.run()
}

fun addCount(count: Int): Int = count + 1

fun addCount(count: Int, function: (Int) -> Int): Int = function(count)

class WindowCreation : App(true, true) {

    override fun init(): Boolean {
        if(super.init()) {
            initGui(ExampleGui(this))
            return true
        }
        return false
    }

    override fun loadSettings(): Settings {
        val settings = Settings()

        settings["mipmap"] = IntSetting(0)
        settings["oversample"] = IntSetting(1)
        settings["icon"] = StringSetting("resources/boilermake.png")

        //OPTIONAL, IN MOST CASES IT'S BEST TO LET DEFAULT BEHAVIOR PERSIST TO HAVE SAVED STATES
        settings.windowMode = WindowMode.WINDOWED

        settings.title = "Window Creation Test"

        return settings
    }
}