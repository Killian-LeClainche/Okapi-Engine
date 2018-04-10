package polaris.okapi.gui

import polaris.okapi.App
import polaris.okapi.gui.content.GuiContent
import polaris.okapi.options.WindowMode
import polaris.okapi.util.inBounds
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import java.util.ArrayList

/**
 * Created by Killian Le Clainche on 12/10/2017.
 */

abstract class GuiScreen : Gui {

    private var elementList: MutableList<GuiContent> = ArrayList()
    private var elementCounter = 0

    var focusedElement: GuiContent? = null
        protected set

    val size: Int
        get() = elementList.size

    constructor(app: App) : super(app)
    constructor(gui: GuiScreen) : super(gui.application, gui, 0.0)

    override fun render(delta: Double) {
        super.render(delta)

        val mouseX = settings.mouse.x
        val mouseY = settings.mouse.y

        for (element in elementList) {
            if (inBounds(mouseX, mouseY, element.bounds)) {
                //flag = element.handleInput();
            }
            element.update(delta)
        }

        for (element in elementList) {
            element.render(delta)
        }
    }

    override fun close() {
        this.focusedElement = null
    }

    fun mouseClick(mouseId: Int): Boolean {
        for (element in elementList) {
            if (inBounds(settings.mouse.x, settings.mouse.y, element.bounds)) {
                val flag = element.nMouseClick(mouseId)
                if (flag && element !== focusedElement) {
                    unbindCurrentElement(element)
                }
                return flag
            }
        }
        unbindCurrentElement()
        return false
    }

    fun unbindCurrentElement(e: GuiContent) {
        unbindCurrentElement()
        focusedElement = e
    }

    fun unbindCurrentElement() {
        if (focusedElement != null) {
            focusedElement!!.unbind()
            focusedElement = null
        }
    }

    fun mouseHeld(mouseId: Int) {
        if (focusedElement != null && focusedElement!!.nMouseHeld(mouseId)) {
            unbindCurrentElement()
        }
    }

    fun mouseRelease(mouseId: Int) {
        if (focusedElement != null && !focusedElement!!.nMouseRelease(mouseId)) {
            unbindCurrentElement()
        }
    }

    fun mouseScroll(xOffset: Double, yOffset: Double) {
        if (focusedElement != null && focusedElement!!.nMouseScroll(xOffset, yOffset)) {
            unbindCurrentElement()
        }
    }

    fun keyPressed(keyId: Int, mods: Int): Int {
        if (focusedElement != null) {
            return focusedElement!!.nKeyPressed(keyId, mods)
        }
        if (keyId == GLFW_KEY_ESCAPE) {
            if (mods and 1 == 1) {
                settings.windowMode = WindowMode.WINDOWED
            } else {
                if (parent != null) {
                    parent.reinit()
                    application.reinitGui(parent)
                    return 0
                } else {
                    application.isRunning = false
                }
            }
        }
        return -1
    }

    fun keyHeld(keyId: Int, called: Int, mods: Int): Int {
        return if (focusedElement != null) {
            focusedElement!!.nKeyHeld(keyId, called, mods)
        } else -1
    }

    fun keyRelease(keyId: Int, mods: Int) {
        if (focusedElement != null && focusedElement!!.nKeyRelease(keyId, mods)) {
            unbindCurrentElement()
        }
    }

    fun addElement(e: GuiContent) {
        e.init(this, elementCounter)
        elementCounter++
        elementList.add(e)
    }

    fun removeElement(i: Int) {
        elementList.removeAt(i).close()
    }

    fun removeElements(i: Int, i1: Int) {
        for (j in i1 - 1 downTo i) {
            elementList.removeAt(j).close()
        }
    }

    fun elementUpdate(e: GuiContent, actionId: Int) {}

    fun clearElements() {
        elementList.clear()
    }

    fun setCurrentElement(id: Int) {
        focusedElement = this.getElement(id)
    }

    fun getElement(i: Int): GuiContent {
        return elementList[i]
    }

}