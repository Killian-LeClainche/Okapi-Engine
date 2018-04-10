package polaris.okapi.options

/**
 * Created by Killian Le Clainche on 12/10/2017.
 */

class GameTimer {

    private var start: Long = 0
    private val ticks = LongArray(1000)
    private var first: Int = 0
    private var last: Int = 0
    var fps: Int = 0
        private set

    fun start() {
        this.start = System.nanoTime()
    }

    fun tick() {
        ticks[last] = System.nanoTime()
        while (ticks[last] - ticks[first] >= 1000000000L) {
            first = circular(first + 1)
            fps--
        }
        last = circular(last + 1)
        fps++
    }

    fun tock(): Long {
        return System.nanoTime() - ticks[circular(last - 1)]
    }

    private fun circular(n: Int): Int {
        return Math.floorMod(n, ticks.size)
    }
}