package polaris.okapi.world

import polaris.okapi.App
import polaris.okapi.options.Settings
import polaris.okapi.render.Texture

/**
 * Created by Killian Le Clainche on 12/10/2017.
 */
abstract class World @JvmOverloads constructor(val application: App, val parent: World? = null, protected var ticksExisted: Int = 0) {

    val settings: Settings = application.settings

    protected val worldTextures: MutableMap<String, Texture> = HashMap()

    operator fun get(textureName: String, textureLocation: String): Texture {
        if(!worldTextures.containsKey(textureName)) {
            application.textureManager[textureName] = textureLocation
            worldTextures[textureName] = application.textureManager[textureName]
        }

        return worldTextures[textureName]!!
    }

    open fun init() {}

    open fun update() {
        ticksExisted++
    }

    open fun render(delta: Double) {
        application.gl2d()
    }

    open fun reinit() {}

    open fun reload() {}

    open fun close() {}

    @JvmOverloads fun destroy(keepTextureList: Array<String>? = null) {
        if(keepTextureList == null) {
            worldTextures.forEach {
                application.textureManager -= it.key
            }
        }
    }

}

