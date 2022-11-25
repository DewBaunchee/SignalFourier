package domain.smooth

import domain.signal.Signal

class ParabolaSmooth : SignalSmoothing {

    private val m = 6
    private val qualifier = 1.0 / 2431

    override fun name(): String {
        return "Parabola"
    }

    override fun smooth(signal: Signal): Signal {
        return Signal(
            signal.n,
            List(signal.length) { i ->
                if (isInRange(i, signal.length))
                    Signal.Part(
                        i,
                        qualifier * (
                            110 * signal.value(i - 6)
                                - 198 * signal.value(i - 5)
                                - 135 * signal.value(i - 4)
                                + 110 * signal.value(i - 3)
                                + 390 * signal.value(i - 2)
                                + 600 * signal.value(i - 1)
                                + 677 * signal.value(i)
                                + 600 * signal.value(i + 1)
                                + 390 * signal.value(i + 2)
                                + 110 * signal.value(i + 3)
                                - 135 * signal.value(i + 4)
                                - 198 * signal.value(i + 5)
                                + 110 * signal.value(i + 6)
                            )
                    )
                else
                    signal.data[i]
            }
        )
    }

    private fun isInRange(index: Int, length: Int): Boolean {
        return (index - m) >= 0 && (index + m) < length
    }
}