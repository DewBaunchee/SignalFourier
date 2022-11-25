package domain.signal

import domain.complex.Complex

class Signal(val n: Int, val data: List<Part>) {

    val length get() = data.size

    fun toComplexArray(): Array<Complex> {
        return Array(data.size) { data[it].toComplex() }
    }

    fun value(index: Int): Double {
        return data[index].value
    }

    data class Part(val i: Int, val value: Double) {

        fun toComplex(): Complex {
            return Complex(value, 0.0)
        }
    }
}