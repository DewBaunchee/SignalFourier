package by.varyvoda.fourier.domain.filter

import by.varyvoda.fourier.domain.signal.Spectre

class SpectreFilter {

    fun filter(spectre: Spectre, min: Double, max: Double): Spectre {
        return Spectre(
            spectre.n,
            spectre.data.map {
                if (it.f < min || it.f > max)
                    Spectre.Part(
                        it.f,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                    )
                else it
            }
        )
    }
}