package domain.dpf

import domain.fourier.Fourier
import domain.signal.Signal
import domain.signal.Spectre
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DPF(
    private val sinTable: TrigonometricTable,
    private val cosTable: TrigonometricTable,
    private val n: Int
) : Fourier {

    companion object {

        fun createFor(jRange: IntRange, n: Int): DPF {
            val range = min(0, jRange.first * n)..(jRange.last * n)
            return DPF(
                TrigonometricTable.createFor(range) { sin(2 * PI * it / n) },
                TrigonometricTable.createFor(range) { cos(2 * PI * it / n) },
                n
            )
        }
    }

    override fun spectre(fRange: IntRange, signal: Signal): Spectre {
        return Spectre(signal.n, fRange.map { f -> direct(f, signal) })
    }

    private fun direct(f: Int, signal: Signal): Spectre.Part {
        val qualifier = 2.0 / n
        var re = 0.0
        var im = 0.0
        signal.data.forEach { part ->
            re += part.value * cosTable.get((part.i % signal.n) * f)
            im += part.value * sinTable.get((part.i % signal.n) * f)
        }
        return Spectre.Part(f, qualifier * re, qualifier * im)
    }
}