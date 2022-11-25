package domain

import kotlin.math.PI
import kotlin.math.cos

class Variant(
    val signalFunction: SignalFunction,
    val amplitudes: List<Int>,
    val phases: List<Double>,
    val slippingWindowSize: Int,
    val medianWindowSize: Int
) {

    companion object {

        private val phases = pi(1.0 / 6, 1.0 / 4, 1.0 / 3, 1.0 / 2, 3.0 / 4, 1.0)

        private val variants = listOf(
            Variant(
                { i, n -> 10 * cos(2 * PI * i / n) },
                listOf(1, 3, 5, 8, 10, 12, 16),
                phases, 3, 5
            ),
            Variant(
                { i, n -> 10 * cos(2 * PI * i / n - PI / 2) },
                listOf(1, 2, 5, 7, 9, 13, 18),
                phases, 5, 7
            ),
            Variant(
                { i, n -> 20 * cos(2 * PI * 20 * i / n) },
                listOf(1, 3, 4, 10, 11, 14, 17),
                phases, 7, 9
            ),
            Variant(
                { i, n -> 100 * cos(2 * PI * 20 * i / n - PI / 4) },
                listOf(2, 3, 5, 9, 10, 12, 15),
                phases, 9, 5
            ),
            Variant(
                { i, n -> 30 * cos(2 * PI * i / n - 3 * PI / 4) },
                listOf(3, 5, 6, 8, 10, 13, 16),
                phases, 3, 7
            ),
            Variant(
                { i, n -> 50 * cos(2 * PI * i / n - PI / 3) },
                listOf(1, 5, 7, 8, 9, 10, 17),
                phases, 5, 9
            ),
        )

        fun of(number: Int): Variant {
            return variants[number - 1]
        }

        private fun pi(vararg values: Double): List<Double> {
            return values.map { it * PI }
        }
    }
}

typealias SignalFunction = (Int, Int) -> Double