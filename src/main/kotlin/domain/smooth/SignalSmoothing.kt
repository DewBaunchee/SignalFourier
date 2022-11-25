package domain.smooth

import domain.signal.Signal

interface SignalSmoothing {

    fun name(): String

    fun smooth(signal: Signal): Signal
}