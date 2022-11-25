package by.varyvoda.fourier.domain.signal.generator

import by.varyvoda.fourier.domain.SignalFunction

class SignalFunctionGenerator(n: Int, private val signalFunction: SignalFunction) : AbstractGenerator(n) {

    override fun value(i: Int): Double {
        return signalFunction(i, n)
    }
}