package by.varyvoda.fourier.domain.fft

import by.varyvoda.fourier.domain.complex.Complex
import by.varyvoda.fourier.domain.fourier.Fourier
import by.varyvoda.fourier.domain.signal.Signal
import by.varyvoda.fourier.domain.signal.Spectre
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class FFT : Fourier {

    override fun spectre(fRange: IntRange, signal: Signal): Spectre {
        return Spectre(signal.n, spectre(signal.toComplexArray()))
    }

    private fun spectre(signal: Array<Complex>): Array<Complex> {
        val n = signal.size

        if (n == 1) return arrayOf(signal[0])

        require(n % 2 == 0) { "n is not a power of 2" }

        val even = spectre(Array(n / 2) { k -> signal[2 * k] })
        val odd = spectre(Array(n / 2) { k -> signal[2 * k + 1] })

        val y = arrayOfNulls<Complex>(n)
        for (k in 0 until n / 2) {
            y[k] = (-2 * k * PI / n).let {
                Complex(cos(it), sin(it)).times(odd[k])
            }.plus(even[k])
            y[k + n / 2] = (-2 * (k + n / 2) * PI / n).let {
                Complex(cos(it), sin(it)).times(odd[k])
            }.plus(even[k])
        }
        return y.requireNoNulls()
    }
}