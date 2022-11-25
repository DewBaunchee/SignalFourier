package domain.complex

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Complex(val r: Double, val i: Double) {

    fun plus(complex: Complex): Complex {
        return Complex(r + complex.r, i + complex.i)
    }

    fun plus(value: Double): Complex {
        return Complex(r + value, i)
    }

    fun minus(complex: Complex): Complex {
        return Complex(r - complex.r, i - complex.i)
    }

    fun times(complex: Complex): Complex {
        return Complex(r * complex.r - i * complex.i, r * complex.i + i * complex.r)
    }

    fun times(value: Double): Complex {
        return Complex(r * value, i * value)
    }

    fun module(): Double {
        return sqrt(r.pow(2) + i.pow(2))
    }

    override fun toString(): String {
        return "$r ${if (i < 0) "-" else "+"} ${abs(i)}i"
    }
}