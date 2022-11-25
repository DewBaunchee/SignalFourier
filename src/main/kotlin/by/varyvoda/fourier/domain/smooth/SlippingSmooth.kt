package by.varyvoda.fourier.domain.smooth

import by.varyvoda.fourier.domain.signal.Signal

class SlippingSmooth(windowSize: Int) : SignalSmoothing {

    init {
        if (windowSize % 2 != 1) throw Exception()
    }

    private val k = windowSize
    private val m = (k - 1) / 2

    override fun name(): String {
        return "Slipping"
    }

    override fun smooth(signal: Signal): Signal {
        return Signal(
            signal.n,
            List(signal.data.size) { i ->
                if (isInRange(i, signal.length))
                    Signal.Part(
                        i,
                        range(i)
                            .map { signal.data[it].value }
                            .average()
                    )
                else
                    signal.data[i]
            }
        )
    }

    private fun isInRange(index: Int, length: Int): Boolean {
        return (index - m) >= 0 && (index + m) < length
    }

    private fun range(index: Int): IntRange {
        return (index - m)..(index + m)
    }
}