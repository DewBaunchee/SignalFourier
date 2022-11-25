package by.varyvoda.fourier.domain.smooth

import by.varyvoda.fourier.domain.signal.Signal

interface SignalSmoothing {

    fun name(): String

    fun smooth(signal: Signal): Signal
}