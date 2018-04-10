package polaris.okapi.util

import java.util.concurrent.atomic.AtomicReference
import java.util.function.UnaryOperator

/**
 * Created by Killian Le Clainche on 3/7/2018.
 */

open class Transmitter<T>(initialState : T) {

    val live : T = initialState
    val transmit : T = initialState

    fun transmit() {
        transmit
    }

}

/*open class PollingTransmitter<T>(val clearFunction : UnaryOperator<T>) : Transmitter<T>() {

    fun poll() {
        transmit()

        live.updateAndGet(clearFunction)
    }

}*/