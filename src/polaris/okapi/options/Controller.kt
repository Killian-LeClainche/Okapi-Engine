package polaris.okapi.options

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */

class Controller(val id: Int): Setting {

    val keyA: Key = Key(GLFW_GAMEPAD_BUTTON_A)
    val keyB: Key = Key(GLFW_GAMEPAD_BUTTON_B)
    val keyX: Key = Key(GLFW_GAMEPAD_BUTTON_X)
    val keyY: Key = Key(GLFW_GAMEPAD_BUTTON_Y)
    val keyLeftBumper: Key = Key(4)
    val keyRightBumper: Key = Key(5)
    val keyBack: Key = Key(6)
    val keyStart: Key = Key(7)
    val keyLeftThumb: Key = Key(8)
    val keyRightThumb: Key = Key(9)
    val keyDPadUp: Key = Key(10)
    val keyDPadRight: Key = Key(11)
    val keyDPadDown: Key = Key(12)
    val keyDPadLeft: Key = Key(13)

    val keyList: List<Key> = listOf(keyA, keyB, keyX, keyY, keyLeftBumper, keyRightBumper, keyBack, keyStart, keyLeftThumb, keyRightThumb, keyDPadUp, keyDPadRight, keyDPadDown, keyDPadLeft)

    //left joystick x + y (0, 1), right joystick x + y (2, 5), left trigger (3), right trigger (4)
    var joysticks: MutableList<Float> = ArrayList()

    var isPresent: Boolean = true
    var hasButtons: Boolean = true
    var hasJoysticks: Boolean = true

    override fun load(value: String) {}

    override fun save(): String {
        return "controller"
    }

    fun update() {
        isPresent = glfwJoystickPresent(id)
        if(!isPresent)
            return

        val buttons = glfwGetJoystickButtons(id)
        hasButtons = buttons != null
        if(hasButtons) {
            for(i in keyList) {
                val pressed = buttons!!.get(i.key) == 1.toByte()
                i.update()
                if(pressed && !i.isPressed)
                    i.press()
                else if(!pressed && i.isPressed)
                    i.release()
            }
        }

        val axes = glfwGetJoystickAxes(id)
        hasJoysticks = axes != null
        if(hasJoysticks) {
            joysticks = ArrayList()
            for(i in 0 until axes!!.limit()) {
                joysticks.add(axes.get(i))
            }
        }
    }
}