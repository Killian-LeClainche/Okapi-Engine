package polaris.okapi.options

/**
 * Created by Killian Le Clainche on 12/11/2017.
 */

interface Setting {
    fun load(value : String)
    fun save() : String
}

class IntSetting(var value: Int) : Setting {

    override fun load(value: String) {
        this.value = value.toInt()
    }

    override fun save(): String = this.value.toString()
}

class LongSetting(var value: Long) : Setting {
    override fun load(value: String) {
        this.value = value.toLong()
    }

    override fun save(): String = this.value.toString()
}

class DoubleSetting(var value: Double) : Setting {

    override fun load(value: String) {
        this.value = value.toDouble()
    }

    override fun save() : String = this.value.toString()

}

class StringSetting(var value: String) : Setting {
    override fun load(value: String) {
        this.value = value
    }

    override fun save() : String = this.value
}