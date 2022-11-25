package domain.signal.generator

import domain.SignalFunction

class SignalFunctionGenerator(n: Int, private val signalFunction: SignalFunction) : AbstractGenerator(n) {

    override fun value(i: Int): Double {
        return signalFunction(i, n)
    }
}