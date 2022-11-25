package by.varyvoda.fourier.domain.smooth

import by.varyvoda.fourier.domain.signal.Signal

class MedianSmooth(windowSize: Int, removingElements: Int) : SignalSmoothing {

    init {
        require(removingElements < windowSize) {}
    }

    private val n = windowSize
    private val k = removingElements
    private val m = (n - 1) / 2 + k

    override fun name(): String {
        return "Median"
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
                            .dropLast(k)
                            .drop(k)
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