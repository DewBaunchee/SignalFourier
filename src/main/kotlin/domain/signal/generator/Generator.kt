package domain.signal.generator

import domain.signal.Signal

interface Generator {

    fun value(i: Int): Double

    fun period(count: Int = 1): Signal
}