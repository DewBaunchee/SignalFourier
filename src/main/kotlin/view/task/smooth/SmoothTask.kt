package view.task.smooth

import domain.Variant
import domain.fft.FFT
import domain.fourier.Fourier
import domain.signal.Signal
import domain.signal.generator.Generator
import domain.signal.generator.SmoothTaskGenerator
import domain.smooth.MedianSmooth
import domain.smooth.ParabolaSmooth
import domain.smooth.SignalSmoothing
import domain.smooth.SlippingSmooth
import javafx.event.EventHandler
import javafx.scene.chart.XYChart
import javafx.scene.control.CheckBox
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import org.controlsfx.control.RangeSlider
import tornadofx.*
import view.standardSpacing
import view.task.Task
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.roundToInt

class SmoothTask(variant: Variant) : Task(variant) {

    private lateinit var signals: XYChart<Number, Number>
    private lateinit var spectres: XYChart<Number, Number>

    private lateinit var nSlider: Slider
    private lateinit var bSlider: RangeSlider

    private lateinit var enableSlippingSmoothCheckBox: CheckBox
    private lateinit var enableParabolaSmoothCheckBox: CheckBox
    private lateinit var enableMedianSmoothCheckBox: CheckBox

    private val isSlippingEnabled get() = enableSlippingSmoothCheckBox.isSelected
    private val isParabolaEnabled get() = enableParabolaSmoothCheckBox.isSelected
    private val isMedianEnabled get() = enableMedianSmoothCheckBox.isSelected

    private val n get() = Parameters.n(nSlider.value)
    private val b1 get() = bSlider.highValue
    private val b2 get() = bSlider.lowValue
    private val fRange get() = 0 until 100

    private lateinit var signal: Signal

    private fun updateSignal() {
        if (!initialized) return
        signal = createGenerator().period()
    }

    private fun createGenerator(): Generator {
        return SmoothTaskGenerator(n, b1, b2)
    }

    init {
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ENTER) {
                updateSignal()
                update()
            }
        }

        hbox {
            standardSpacing()

            vbox {
                standardSpacing()
                hgrow = Priority.ALWAYS

                signals = chart("i")
                spectres = chart("f")

                hbox {
                    standardSpacing()

                    enableSlippingSmoothCheckBox = checkbox("Slipping smooth") { onAction = EventHandler { update() } }
                    enableParabolaSmoothCheckBox = checkbox("Parabola smooth") { onAction = EventHandler { update() } }
                    enableMedianSmoothCheckBox = checkbox("Median smooth") { onAction = EventHandler { update() } }

                    nSlider = Slider(
                        Parameters.nQualifierRange.start,
                        Parameters.nQualifierRange.endInclusive,
                        Parameters.nQualifierRange.endInclusive / 2
                    )
                    nSlider.hgrow = Priority.ALWAYS
                    add(nSlider)
                    label {
                        nSlider.valueProperty().listenValue { _, _, _ ->
                            text = "n = " + Parameters.n(nSlider.value)
                            updateSignal()
                            update()
                        }
                    }
                }

                hbox {
                    standardSpacing()

                    bSlider = RangeSlider(
                        Parameters.bQualifierRange.start,
                        Parameters.bQualifierRange.endInclusive,
                        Parameters.bQualifierRange.start,
                        Parameters.bQualifierRange.endInclusive
                    )
                    bSlider.hgrow = Priority.ALWAYS
                    add(bSlider)

                    val format = DecimalFormat("0.####")

                    label {
                        bSlider.highValueProperty().listenValue { _, _, _ ->
                            text = "b1 = ${format.format(b1)}"
                            updateSignal()
                            update()
                        }
                    }
                    label {
                        bSlider.lowValueProperty().listenValue { _, _, _ ->
                            text = "b2 = ${format.format(b2)}"
                            updateSignal()
                            update()
                        }
                    }
                }
            }
        }

        initialized = true
        updateSignal()
        update()
    }

    override fun update() {
        if (!initialized) return

        val range = fRange
        val fourier = FFT()
        val spectre = fourier.spectre(range, signal)

        spectres.drawSpectre(spectre, false)
        signals.data.setAll(signal.toSeries("Signal"))

        if (isSlippingEnabled) smooth(signal, SlippingSmooth(variant.slippingWindowSize), fRange, fourier)
        if (isParabolaEnabled) smooth(signal, ParabolaSmooth(), fRange, fourier)
        if (isMedianEnabled) smooth(signal, MedianSmooth(variant.medianWindowSize, 2), fRange, fourier)
    }

    private fun smooth(signal: Signal, smoothing: SignalSmoothing, fRange: IntRange, fourier: Fourier) {
        val smoothed = smoothing.smooth(signal)
        signals.data.add(smoothed.toSeries(smoothing.name()))

        val spectre = fourier.spectre(fRange, signal)
        val aj = XYChart.Series<Number, Number>().also { it.name = "${smoothing.name()}: a" }
        val phi = XYChart.Series<Number, Number>().also { it.name = "${smoothing.name()}: phi" }
        spectre.data.forEach {
            aj.data.add(XYChart.Data(it.f, it.a))
            phi.data.add(XYChart.Data(it.f, it.phi))
        }
        spectres.data.addAll(aj, phi)
    }

    class Parameters {
        companion object {

            val bQualifierRange = 0.001..1.0

            val nQualifierRange = 0.0..4.0

            fun n(nQualifier: Double): Int {
                return (2.0.pow(nQualifier.roundToInt()) * 16).toInt()
            }
        }
    }
}