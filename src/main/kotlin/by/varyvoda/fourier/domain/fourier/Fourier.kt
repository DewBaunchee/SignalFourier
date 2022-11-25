package by.varyvoda.fourier.domain.fourier

import by.varyvoda.fourier.domain.signal.Signal
import by.varyvoda.fourier.domain.signal.Spectre
import kotlin.math.PI
import kotlin.math.cos

interface Fourier {

    fun spectre(fRange: IntRange, signal: Signal): Spectre

    fun signal(
        spectre: Spectre,
        addInitial: Boolean,
        withoutPhase: Boolean
    ): Signal {
        val n = spectre.n
        val fRange = (if (addInitial) 1 else 0) until (spectre.data.size / 2 - 1)
        return Signal(n, List(n) { i ->
            Signal.Part(
                i,
                (if (addInitial) spectre.a(0) / 2 else 0.0) +
                    fRange.sumOf { f ->
                        spectre.a(f) * cos(2 * PI * i * f / n - (if (withoutPhase) 0.0 else spectre.phi(f)))
                    }
            )
        })
    }
}