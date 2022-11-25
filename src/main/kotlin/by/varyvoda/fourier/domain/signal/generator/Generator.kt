package by.varyvoda.fourier.domain.signal.generator

import by.varyvoda.fourier.domain.signal.Signal

interface Generator {

    fun value(i: Int): Double

    fun period(count: Int = 1): Signal
}