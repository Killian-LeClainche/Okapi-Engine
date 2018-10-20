package polaris.okapi.gui

import polaris.okapi.App
import polaris.okapi.options.Settings
import polaris.okapi.render.*
import polaris.okapi.world.World

/**
 * Created by Killian Le Clainche on 12/10/2017.
 */
abstract class Gui @JvmOverloads constructor(protected val application: App, val parent: Gui? = null, protected var ticksExisted: Double = 0.0) {
    val settings: Settings = application.settings
    var world: World? = application.currentWorld
    val renderer: RenderManager = application.renderManager
    val textures: TextureManager = application.textureManager
    val fonts: FontManager = application.fontManager
    val models: ModelManager = application.modelManager

    open fun init() {}

    open fun render(delta: Double) {
        application.updateView()
        ticksExisted += delta
    }

    open fun reinit() {}

    open fun reload() {}

    open fun close() {}

}