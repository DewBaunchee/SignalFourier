package domain.signal

import domain.complex.Complex
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class Spectre(val n: Int, val data: List<Part>) {

    constructor(n: Int, complex: Array<Complex>) : this(n, complex.mapIndexed { i, item -> Part(n, i, item) })

    fun a(f: Int): Double {
        return data[f].a
    }

    fun phi(f: Int): Double {
        return data[f].phi
    }

    data class Part(
        val f: Int,
        val aC: Double,
        val aS: Double,
        val a: Double = sqrt(aC.pow(2) + aS.pow(2)),
        val phi: Double = atan2(aS, aC)
    ) {

        constructor(n: Int, f: Int, complex: Complex) : this(
            f,
            0.0,
            0.0,
            2 * complex.module() / n,
            -atan2(complex.i, complex.r)
        )
    }
}