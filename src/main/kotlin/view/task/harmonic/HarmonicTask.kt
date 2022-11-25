package view.task.harmonic

import domain.Variant
import domain.dpf.DPF
import domain.fft.FFT
import domain.fourier.Fourier
import domain.signal.Spectre
import domain.signal.generator.Generator
import domain.signal.generator.SignalFunctionGenerator
import javafx.scene.chart.XYChart
import javafx.scene.control.CheckBox
import javafx.scene.control.Slider
import javafx.scene.layout.Priority
import org.controlsfx.control.RangeSlider
import tornadofx.*
import view.standardSpacing
import view.task.Task
import kotlin.math.round
import kotlin.math.roundToInt

class HarmonicTask(variant: Variant) : Task(variant) {

    private lateinit var signals: XYChart<Number, Number>
    private lateinit var spectres: XYChart<Number, Number>

    private lateinit var j: RangeSlider
    private lateinit var nSlider: Slider

    private lateinit var isFastCheckBox: CheckBox

    val n get() = nSlider.value.roundToInt()
    private val jRange get() = j.range()
    private val isFast get() = isFastCheckBox.isSelected

    init {
        vbox {
            standardSpacing()

            signals = chart("i")
            spectres = chart("f")

            hbox {
                standardSpacing()

                j = RangeSlider()
                j.hgrow = Priority.ALWAYS
                add(j)
                label {
                    j.lowValueProperty().listenValue { _, _, _ ->
                        text = "j = " + j.range()
                        update()
                    }
                    j.highValueProperty().addListener { _, _, _ ->
                        text = "j = " + j.range()
                        update()
                    }
                }
            }
            hbox {
                standardSpacing()

                isFastCheckBox = checkbox {
                    text = "Fast Fourier"
                    selectedProperty().addListener { _, _, _ -> update() }
                }

                nSlider =
                    Slider(Parameters.nRange.start, Parameters.nRange.endInclusive, Parameters.nRange.endInclusive / 2)
                nSlider.hgrow = Priority.ALWAYS
                add(nSlider)
                label {
                    nSlider.valueProperty().listenValue { _, _, _ ->
                        text = "n = " + nSlider.value.roundToInt()
                        j.min = Parameters.jMin()
                        j.max = Parameters.jMax(nSlider.value)
                        j.lowValue = j.min
                        j.highValue = j.max
                        update()
                    }
                }
            }
        }

        initialized = true
        update()
    }

    private fun createGenerator(): Generator {
        return SignalFunctionGenerator(n, variant.signalFunction)
    }

    override fun update() {
        if (!initialized) return

        val n = n
        val signal = createGenerator().period()

        signals.data.setAll(signal.toSeries("Signal"))

        val range = jRange
        val fourier = if (isFast) FFT() else DPF.createFor(range, n)

        val spectre = fourier.spectre(range, signal)

        spectres.drawSpectre(spectre)

        reconstruct(fourier, spectre)
    }

    private fun reconstruct(fourier: Fourier, spectre: Spectre) {
        signals.data.add(fourier.signal(spectre, addInitial = false, withoutPhase = false).toSeries("Reconstructed"))
    }

    class Parameters {

        companion object {

            val nRange = 1.0..128.0

            fun jMin(): Double {
                return 0.0
            }

            fun jMax(n: Double): Double {
                return round(2.0 * n.roundToInt())
            }
        }
    }
}