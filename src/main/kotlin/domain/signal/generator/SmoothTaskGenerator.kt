package domain.signal.generator

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

class SmoothTaskGenerator(n: Int, private val b1: Double, private val b2: Double) : AbstractGenerator(n) {

    override fun value(i: Int): Double {
        return b1 * sin(2 * PI * i / n + (50..70).sumOf { f ->
            (-1.0).pow((0..1).random()) * b2 * sin(2 * PI * i * f / n)
        })
    }
}