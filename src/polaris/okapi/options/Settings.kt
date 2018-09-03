package polaris.okapi.options

import org.joml.Vector2d
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack.stackPush
import polaris.okapi.SCALE_TO_HEIGHT
import polaris.okapi.SCALE_TO_WIDTH
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

/**
 * Created by Killian Le Clainche on 12/10/2017.
 */

class Settings {

    var window: Long = 0L

    val keyboardMapping = HashMap<Int, Key>()
    val mouseMapping = HashMap<Int, Key>()

    val mouse: Vector2d = Vector2d(0.0)
    val mouseDelta: Vector2d = Vector2d(0.0)
    val scrollDelta: Vector2d = Vector2d(0.0)
    private val textInput: StringBuilder = StringBuilder()

    val inputText: String
        get() = textInput.toString()

    /**
     * @return
     */
    var monitor: Monitor? = null
        private set
        get() {
            if(field == null)
                field = getMonitor(glfwGetPrimaryMonitor())

            return field
        }

    /**
     * @return
     */
    var windowMode: WindowMode = WindowMode.WINDOWED
        set(value) {
            if(field != value) {
                field = value
                updateWindow = true
            }
        }

    var updateWindow: Boolean = false
        private set
        get() {
            val shouldUpdate = field
            field = false
            return shouldUpdate
        }

    var windowPosX: Int = -1
        get() {
            if (field == -1 && monitor != null) field = (monitor!!.videoMode.width() - windowWidth) / 2

            return field
        }

    var windowPosY: Int = -1
        get() {
            if (field == -1 && monitor != null) field = (monitor!!.videoMode.height() - windowHeight) / 2

            return field
        }

    var windowWidth: Int = -1
        get() {
            if(field == -1 && monitor != null) field = monitor!!.videoMode.width() / 2

            return field
        }

    var windowHeight: Int = -1
        get() {
            if(field == -1 && monitor != null) field = monitor!!.videoMode.height() / 2

            return field
        }

    var windowMaximized: Boolean = false

    var enableVsync: Boolean = true
    var alcRefreshRate: Int = 60
    var alcSync: Boolean = false

    var settings: MutableMap<String, Setting> = HashMap()

    var title: String = ""

    operator fun get(property : String): Setting? {
        return settings[property]
    }

    operator fun set(property : String, value : Setting) {
        if(value is Key) {
            if(value.key in GLFW_MOUSE_BUTTON_1..GLFW_MOUSE_BUTTON_LAST)
                mouseMapping.put(value.key, value)
            else
                keyboardMapping.put(value.key, value)

        }
        settings[property] = value
    }

    operator fun set(property : String, value : String) {
        settings[property] = StringSetting(value)
    }

    operator fun set(property : String, value : Int) {
        settings[property] = IntSetting(value)
    }

    operator fun set(property : String, value : Double) {
        settings[property] = DoubleSetting(value)
    }

    fun load() {

        (GLFW_MOUSE_BUTTON_1..GLFW_MOUSE_BUTTON_LAST)
                .filterNot { mouseMapping.containsKey(it) }
                .forEach { mouseMapping.put(it, Key(it)) }

        val settingsFile = File("settings.prop")
        if(!settingsFile.createNewFile()) {
            val properties = Properties()
            properties.load(FileInputStream(settingsFile))
            properties.forEach {
                val value = it.value.toString()
                when (it.key) {
                    "enableVsync" -> if(enableVsync) enableVsync = value.toBoolean()
                    "alcRefreshRate" -> if(alcRefreshRate == 60) alcRefreshRate = value.toInt()
                    "alcSync" -> if(!alcSync) alcSync = value.toBoolean()
                    "monitor" -> settings["monitor"] = IntSetting(value.toInt())
                    "windowMode" -> if(!updateWindow) windowMode = WindowMode.valueOf(value.toInt())
                    "windowPosX" -> windowPosX = value.toInt()
                    "windowPosY" -> windowPosY = value.toInt()
                    "windowWidth" -> windowWidth = value.toInt()
                    "windowHeight" -> windowHeight = value.toInt()
                    "windowMaximized" -> windowMaximized = value.toBoolean()
                    settings.containsKey(it.key) -> settings[it.key as String]!!.load(value)
                }
            }
        }
    }

    fun save() {
        val properties = Properties()

        properties["enableVsync"] = enableVsync.toString()
        properties["alcRefreshRate"] = alcRefreshRate.toString()
        properties["alcSync"] = alcSync.toString()

        if(monitor != null)
            properties["monitor"] = "${monitor!!.instance}"

        properties["windowMode"] = WindowMode.valueOf(windowMode).toString()
        properties["windowPosX"] = windowPosX.toString()
        properties["windowPosY"] = windowPosY.toString()
        properties["windowWidth"] = windowWidth.toString()
        properties["windowHeight"] = windowHeight.toString()
        properties["windowMaximized"] = windowMaximized.toString()

        settings.filterNot { properties.containsKey(it.key) }
                .forEach { properties[it.key] = it.value.save() }

        settings.forEach {
            if(!properties.containsKey(it.key)) {
                properties[it.key] = it.value.save()
            }
        }

        val settingsFile = File("settings.prop")
        settingsFile.createNewFile()

        val settingsIO = FileOutputStream(settingsFile)

        properties.store(settingsIO, "Current settings for $title, it is not recommended to alter the values here")

        settingsIO.close()
    }

    fun init(instance: Long) {
        window = instance

        if(enableVsync)
            glfwSwapInterval(1)
        else
            glfwSwapInterval(0)

        stackPush().use({ stack ->
            val width = stack.callocInt(1)
            val height = stack.callocInt(1)

            glfwGetFramebufferSize(window, width, height)

            windowWidth = width[0]
            windowHeight = height[0]
        })

        monitor = getMonitor(glfwGetPrimaryMonitor())

        if(settings["icon"] != null) {
            val icon = GLFWImage.malloc(1)

            stackPush().use {
                val w = it.mallocInt(1)
                val h = it.mallocInt(1)
                val comp = it.mallocInt(1)

                val image = stbi_load((settings["icon"] as StringSetting).value, w, h, comp, 4)
                if(image === null) {
                    System.err.println("Could not load icon!")
                    System.err.println(stbi_failure_reason())
                }
                else {
                    icon[0].set(w[0], h[0], image)
                }
            }

            glfwSetWindowIcon(window, icon)
        }

        glfwSetFramebufferSizeCallback(window, GLFWFramebufferSizeCallback.create { _, _, _ ->
            stackPush().use({ stack ->
                val width = stack.callocInt(1)
                val height = stack.callocInt(1)

                glfwGetWindowSize(window, width, height)

                windowWidth = width[0]
                windowHeight = height[0]
            })
        })

        glfwSetWindowPosCallback(window, GLFWWindowPosCallback.create { _, _posX, _posY ->
            windowPosX = _posX
            windowPosY = _posY
        })

        glfwSetWindowMaximizeCallback(window, GLFWWindowMaximizeCallback.create { _, _maximized ->
            windowMaximized = _maximized

            if(!_maximized) {
                val width = monitor!!.videoMode.width()
                val height = monitor!!.videoMode.height()

                windowWidth = width / 2
                windowHeight = height / 2
                windowPosX = monitor!!.posX + width / 4
                windowPosY = monitor!!.posY + height / 4

                glfwSetWindowSize(window, windowWidth, windowHeight)
                glfwSetWindowPos(window, windowPosX, windowPosY)
            }
        })

        glfwSetCursorPosCallback(window, GLFWCursorPosCallback.create { _, xpos, ypos ->
            val xposAdjusted = (xpos/ windowWidth) * SCALE_TO_WIDTH
            val yposAdjusted = (ypos / windowHeight) * SCALE_TO_HEIGHT

            mouseDelta.set(mouse.sub(xposAdjusted, yposAdjusted))
            mouse.set(xposAdjusted, yposAdjusted)
        })

        glfwSetMouseButtonCallback(window, GLFWMouseButtonCallback.create { _, button, action, _ ->
            if(button != -1) {
                val mouseKey = mouseMapping[button]

                if (action == GLFW_PRESS)
                    mouseKey?.press()
                else if (action == GLFW_RELEASE)
                    mouseKey?.release()
            }
        })

        glfwSetScrollCallback(window, GLFWScrollCallback.create { _, xoffset, yoffset -> scrollDelta.add(xoffset, yoffset) })

        glfwSetKeyCallback(window, GLFWKeyCallback.create { _, key, _, action, _ ->
            if (key != -1) {
                val keyboardKey = keyboardMapping[key]

                if (action == GLFW_PRESS)
                    keyboardKey?.press()
                else if (action == GLFW_RELEASE)
                    keyboardKey?.release()
            }
        })

        glfwSetCharCallback(window, GLFWCharCallback.create { _, codepoint -> textInput.append(codepoint.toChar()) })
    }

    fun update() {
        textInput.setLength(0)
        mouseDelta.set(0.0)
        scrollDelta.set(0.0)
    }

    fun poll() {

    }


    fun setCursorMode(windowInstance: Long, mode: Int) {
        glfwSetInputMode(windowInstance, GLFW_CURSOR, mode)
    }

    companion object {

        private var staticInitialized = false
        private var monitorList: MutableList<Monitor> = ArrayList()

        fun init() {
            if(staticInitialized) return

            staticInitialized = true

            glfwSetMonitorCallback(GLFWMonitorCallback.create { monitor, event ->
                if (event == GLFW_CONNECTED) {
                    val monitorObject = createMonitor(monitor)

                    if(monitorObject != null)
                        monitorList.add(monitorObject)
                }
                else
                    monitorList = monitorList.filterNot { it.instance == monitor }.toMutableList()
            })

            val buffer = glfwGetMonitors()

            if(buffer != null)
                (0 until buffer.capacity()).forEach {
                    val monitorObject = createMonitor(buffer.get(it))

                    if(monitorObject != null)
                        monitorList.add(monitorObject)
                }
        }

        fun hasMonitors() : Boolean = monitorList.isNotEmpty()

        fun getMonitor(index: Int) : Monitor = monitorList[index]

        fun getMonitor(instance: Long): Monitor? = monitorList.singleOrNull { it.instance == instance }
    }

}
