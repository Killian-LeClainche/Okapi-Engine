package polaris.okapi.options

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWGammaRamp
import org.lwjgl.glfw.GLFWVidMode

/**
 * Created by Killian Le Clainche on 12/11/2017.
 */

fun createMonitor(monitorInstance : Long) : Monitor? {
    val name: String = glfwGetMonitorName(monitorInstance) ?: return null
    val physicalSizeX = IntArray(1)
    val physicalSizeY = IntArray(1)
    val xpos = IntArray(1)
    val ypos = IntArray(1)
    val videoMode: GLFWVidMode = glfwGetVideoMode(monitorInstance) ?: return null
    val gammaRamp: GLFWGammaRamp = glfwGetGammaRamp(monitorInstance) ?: return null

    glfwGetMonitorPhysicalSize(monitorInstance, physicalSizeX, physicalSizeY)
    glfwGetMonitorPos(monitorInstance, xpos, ypos)

    return Monitor(name, monitorInstance, physicalSizeX[0], physicalSizeY[0], xpos[0], ypos[0], videoMode, gammaRamp)
}

data class Monitor (val name: String, val instance: Long, val sizeX: Int, val sizeY: Int, val posX: Int, val posY: Int, val videoMode: GLFWVidMode, val gammaRamp: GLFWGammaRamp)