package polaris.okapi.options

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetKeyName
import java.io.Serializable

/**
 * Created by Killian Le Clainche on 12/11/2017.
 */
open class Key(key: Int) : Setting {

    var name: String? = glfwGetKeyName(key, 0)
        private set

    var key: Int = key
        set(value) {
            field = value
            name = glfwGetKeyName(field, 0)
        }

    var isPressed: Boolean = false
        private set

    var isClicked: Boolean = false
        private set

    var wasQuickPressed: Boolean = false

    private var pressTimer: Long = 0

    var isDoublePressed: Boolean = false
        private set

    val pressedTime: Long
        get() = System.nanoTime() - pressTimer

    override fun load(value: String) {
        key = value.toInt()
    }

    override fun save(): String {
        return key.toString()
    }

    fun press() {
        isPressed = true
        wasQuickPressed = false

        val time = System.nanoTime()

        if (time - pressTimer < KEY_DOUBLE_PRESS) isDoublePressed = true

        pressTimer = time
    }

    fun release() {
        isPressed = false
        isDoublePressed = false
        isClicked = true

        if (pressedTime <= KEY_DOUBLE_PRESS) wasQuickPressed = true
    }

    fun update() {
        isClicked = false
        wasQuickPressed = false
    }

    override fun toString(): String {
        return "Key: $name, Key Code: $key, isPressed: $isPressed, wasQuickPressed: $wasQuickPressed, \n\t pressTimer: $pressTimer, isDoublePressed: $isDoublePressed"
    }

    companion object {

        private const val KEY_DOUBLE_PRESS = (1000000000 / 5).toLong()
    }
}