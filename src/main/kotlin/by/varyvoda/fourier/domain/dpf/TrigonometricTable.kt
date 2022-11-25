package by.varyvoda.fourier.domain.dpf

class TrigonometricTable(private val range: IntProgression, private val values: List<Double>) {

    companion object {

        fun createFor(range: IntProgression, func: (Int) -> Double): TrigonometricTable {
            return TrigonometricTable(range, (0..(range.last - range.first)).map(func))
        }
    }

    fun get(index: Int): Double {
        return values[index - range.first]
    }
}