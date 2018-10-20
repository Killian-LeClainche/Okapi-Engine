package polaris.okapi

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.Configuration
import org.lwjgl.system.rpmalloc.RPmalloc.*
import polaris.okapi.gui.Gui
import polaris.okapi.options.GameTimer
import polaris.okapi.options.Settings
import polaris.okapi.options.WindowMode
import polaris.okapi.render.FontManager
import polaris.okapi.render.ModelManager
import polaris.okapi.render.RenderManager
import polaris.okapi.render.TextureManager
import polaris.okapi.sound.SoundManager
import polaris.okapi.world.World
import java.util.concurrent.ExecutionException
import kotlin.concurrent.thread

/**
 * The dimensions that all rendering should be bound to, it will automatically scale things properly.
 */
const val SCALE_TO_WIDTH = 1920.toDouble()
const val SCALE_TO_HEIGHT = 1080.toDouble()
/**
 * Static counter meant to determine if glfw should be terminated.
 */
@Volatile private var applicationCount = 0

/**
 * Created by Killian Le Clainche on April 23, 2017.
 * The base file which all applications should extend to provide functionality.
 *
 * @param <T> The Settings Class which should be used for all of the system.
</T> */
abstract class App

/**
 * Protected constructor of the Application.
 * I do this because I don't want people to see this constructor.
 *
 * @param debug     Called from children constructors to determine whether the application should be debugging or
 * not in the console.
 */
protected constructor(debug: Boolean) {

    /**
     * Instance of application's sound system.
     */
    lateinit var soundSystem: SoundManager
        protected set

    /**
     * long instance of the window this application takes on.
     */
    var window: Long = 0
        protected set

    /**
     * The settings of the game, allows for inheritance.
     */
    lateinit var settings: Settings
        protected set

    /**
     * Handles all textures involved in the applications environment. I have to overhaul it again to try and find
     * that perfect balance that I feel comfortable with.
     */
    lateinit var textureManager: TextureManager
        protected set

    lateinit var modelManager: ModelManager
        protected set


    lateinit var fontManager: FontManager
        protected set

    lateinit var renderManager: RenderManager

    /**
     * The protected field that determines if this application should continue to run.
     */
    @Volatile var isRunning: Boolean = false
        @Synchronized set

    /**
     * Instance of current gui being displayed.
     */
    var currentScreen: Gui? = null
        @Synchronized set(value) {
            field?.close()

            value?.init()

            field = value
        }

    /**
     * Instance of current world being rendered.
     */
    var currentWorld: World? = null
        protected set

    /**
     * The change of time since the previous delta setup call
     * (which is basically glfwGetTime(); glfwSetTime(0);)
     */
    var tickDelta: Double = 0.toDouble()
        private set

    //TODO move to Settings
    val maxUPS: Int
        get() = 60

    val windowScaleX: Float
        get() = SCALE_TO_WIDTH.toFloat() / settings.windowWidth.toFloat()

    val windowScaleY: Float
        get() = SCALE_TO_HEIGHT.toFloat() / settings.windowHeight.toFloat()

    private var closedUpdateThread: Boolean = false

    init {
        // Set up debugging if necessary
        Configuration.DISABLE_CHECKS.set(!debug)
        Configuration.DEBUG.set(debug)
        Configuration.GLFW_CHECK_THREAD0.set(!debug)

        window = -1
        tickDelta = 0.0
    }

    protected abstract fun loadSettings(): Settings

    open fun init() : Boolean {

        synchronized(applicationCount) {

            if (applicationCount == 0) {
                rpmalloc_initialize()

                if (!glfwInit()) {
                    System.err.println("Failed to initialize application!")
                    System.err.println("create() method caused crash.")
                    return false
                }

                Settings.init()
            }

            applicationCount++

        }

        settings = loadSettings()

        soundSystem = SoundManager(settings)

        settings.load()
        settings.save()

        if (!create()) {
            System.err.println("Failed to initialize application!")
            System.err.println("setup() method caused crash.")
            return false
        }

        if(!soundSystem.init()) {
            System.err.println("Failed to initialize application!")
            System.err.println("setup() method caused crash.")
            return false
        }

        //TODO Integrate Capabilities into rendering system
        GL.createCapabilities()

        textureManager = TextureManager(settings)
        modelManager = ModelManager(settings)
        fontManager = FontManager(settings)
        renderManager = RenderManager(settings)

        return true
    }

    /**
     * Initializes a window application
     */
    @Throws(ExecutionException::class, InterruptedException::class)
    open fun run() {
        setupGL()

        /*if(currentWorld == null && currentScreen == null) {
            System.err.println("Failed to run application!")
            System.err.println("No instance of world or gui.")
            return
        }*/

        isRunning = true

        thread {
            rpmalloc_thread_initialize()
            updateTick()
            rpmalloc_thread_finalize()
        }

        renderTick()
    }

    protected open fun renderTick() {
        glfwSetTime(0.0)
        glfwMakeContextCurrent(window)

        while(!glfwWindowShouldClose(window) && isRunning) {

            tickDelta = glfwGetTime()

            glfwSetTime(0.0)

            if(updateWindow()) break

            glfwPollEvents()

            glClear(GL_ACCUM_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)

            currentWorld?.render(tickDelta)
            currentScreen?.render(tickDelta)
            glfwSwapBuffers(window)
        }

        isRunning = false

        while(!closedUpdateThread) {
            try {
                Thread.sleep(1)
            }
            catch(e : InterruptedException) {
                destroy()
            }
        }

        destroy()
    }

    protected open fun updateTick() {
        val timer = GameTimer()
        var remainingTime = 0L
        timer.start()

        while(isRunning) {
            timer.tick()

            settings.poll()

            currentWorld?.update()

            settings.mouseMapping.forEach {
                it.value.update()
            }

            settings.keyboardMapping.forEach {
                it.value.update()
            }

            val wakeTime = System.nanoTime() + (1000000000L / maxUPS) - timer.tock() + remainingTime
            remainingTime = (wakeTime - System.nanoTime()) % 1000000L

            try {
                Thread.sleep(Math.max((wakeTime - System.nanoTime()), 0) / 1000000L)
            }
            catch(e : InterruptedException) {
                isRunning = false
            }
        }

        closedUpdateThread = true
    }

    protected open fun setupGL() {
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
    }

    protected open fun destroy() {
        isRunning = false
        settings.save()

        soundSystem.close()

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window)

        synchronized(applicationCount) {
            applicationCount--

            if (applicationCount == 0) {
                glfwTerminate()
            }
        }
    }

    /**
     * sets up the environment for a window to be created.
     *
     * @return true for success, false otherwise
     */
    open fun create(): Boolean {
        val instance: Long

        val gameTitle = settings.title

        val monitor = settings.monitor

        if(monitor == null) {
            destroy()
            return false
        }

        val monitorInstance = monitor.instance
        val videoMode = monitor.videoMode

        val mode = settings.windowMode

        instance = when (mode) {
            WindowMode.WINDOWED -> createWindow(settings.windowWidth, settings.windowHeight)
            WindowMode.FULLSCREEN -> createFullscreen(videoMode, gameTitle, monitorInstance)
            else -> createBorderless(videoMode, gameTitle, monitorInstance)
        }

        if (instance == 0L) {
            destroy()
            return false
        }

        if (window != -1L)
            glfwDestroyWindow(window)

        window = instance

        glfwMakeContextCurrent(window)

        settings.init(window)

        glfwShowWindow(window)

        return true
    }

    @JvmOverloads
    open fun createWindow(width: Int, height: Int, title: String = settings.title, parentInstance: Long = 0): Long {
        glfwWindowHint(GLFW_DECORATED, GLFW_TRUE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE)
        glfwWindowHint(GLFW_MAXIMIZED, if(settings.windowMaximized) GLFW_TRUE else GLFW_FALSE)

        val instance = glfwCreateWindow(width, height, title, 0, parentInstance)

        settings.windowWidth = width
        settings.windowHeight = height

        if(instance != 0L)
            glfwSetWindowPos(instance, settings.windowPosX, settings.windowPosY)

        return instance
    }

    /**
     * Creates the window in borderless mode.
     */
    open fun createBorderless(videoMode: GLFWVidMode, gameTitle: String, monitorInstance: Long): Long {
        glfwWindowHint(GLFW_RED_BITS, videoMode.redBits())
        glfwWindowHint(GLFW_GREEN_BITS, videoMode.greenBits())
        glfwWindowHint(GLFW_BLUE_BITS, videoMode.blueBits())
        glfwWindowHint(GLFW_REFRESH_RATE, videoMode.refreshRate())

        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE)

        return glfwCreateWindow(videoMode.width(), videoMode.height(), gameTitle, 0, 0)
    }

    /**
     * Creates the window in fullscreen mode.
     */
    open fun createFullscreen(videoMode: GLFWVidMode, gameTitle: String, monitorInstance: Long): Long {
        glfwWindowHint(GLFW_RED_BITS, videoMode.redBits())
        glfwWindowHint(GLFW_GREEN_BITS, videoMode.greenBits())
        glfwWindowHint(GLFW_BLUE_BITS, videoMode.blueBits())
        glfwWindowHint(GLFW_REFRESH_RATE, videoMode.refreshRate())
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE)

        return glfwCreateWindow(videoMode.width(), videoMode.height(), gameTitle, monitorInstance, 0)
    }

    protected open fun updateWindow(): Boolean {
        if (settings.updateWindow) {
            System.out.println("Update " + settings.updateWindow);
            val textureData = textureManager.textures

            textureData.forEach {
                it.value.destroy()
            }

            if (!create())
                return true

            textureManager.textures = textureData

            currentScreen?.reload()
        }

        return false
    }

    /**
     * Call before performing 2d rendering
     */
    open fun updateView(){
        glViewport(0, 0, settings.windowWidth, settings.windowHeight)
    }

}
