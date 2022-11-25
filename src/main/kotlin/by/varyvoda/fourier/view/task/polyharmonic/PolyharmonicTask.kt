package by.varyvoda.fourier.view.task.polyharmonic

import by.varyvoda.fourier.domain.SignalFunction
import by.varyvoda.fourier.domain.Variant
import by.varyvoda.fourier.domain.dpf.DPF
import by.varyvoda.fourier.domain.fft.FFT
import by.varyvoda.fourier.domain.filter.SpectreFilter
import by.varyvoda.fourier.domain.fourier.Fourier
import by.varyvoda.fourier.domain.signal.Spectre
import by.varyvoda.fourier.domain.signal.generator.Generator
import by.varyvoda.fourier.domain.signal.generator.SignalFunctionGenerator
import javafx.collections.FXCollections
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.CheckBox
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import org.controlsfx.control.RangeSlider
import tornadofx.*
import by.varyvoda.fourier.view.standardSpacing
import by.varyvoda.fourier.view.task.Task
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt

class PolyharmonicTask(variant: Variant) : Task(variant) {

    private lateinit var signals: XYChart<Number, Number>
    private lateinit var spectres: XYChart<Number, Number>

    private lateinit var nQualifier: Slider
    private lateinit var isFastCheckBox: CheckBox

    private lateinit var filterSlider: RangeSlider
    private lateinit var filterEnabledCheckBox: CheckBox

    private val components = FXCollections.observableArrayList<Spectre.Part>()
    private lateinit var spectre: Spectre

    val n get() = Parameters.n(nQualifier.value)
    private val jRange get() = 0 until n
    private val isFast get() = isFastCheckBox.isSelected

    init {
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ENTER) {
                updateSpectre()
                update()
            }
        }

        hbox {
            standardSpacing()

            tableview(components) {
                readonlyColumn("f", Spectre.Part::f)
                readonlyColumn("A", Spectre.Part::a)
                readonlyColumn("phi", Spectre.Part::phi)
            }

            vbox {
                standardSpacing()
                hgrow = Priority.ALWAYS

                signals = chart("i")
                spectres = chart("f")

                hbox {
                    standardSpacing()

                    isFastCheckBox = checkbox {
                        text = "Fast Fourier"
                        selectedProperty().addListener { _, _, _ -> update() }
                    }

                    nQualifier = Slider(
                        Parameters.nQualifierRange.start,
                        Parameters.nQualifierRange.endInclusive,
                        Parameters.nQualifierRange.endInclusive / 2
                    )
                    nQualifier.hgrow = Priority.ALWAYS
                    add(nQualifier)
                    label {
                        nQualifier.valueProperty().listenValue { _, _, _ ->
                            text = "n = " + Parameters.n(nQualifier.value)
                            update()
                        }
                    }
                }

                hbox {
                    standardSpacing()

                    filterEnabledCheckBox = checkbox {
                        text = "Enable filter"
                        selectedProperty().addListener { _, _, _ -> update() }
                    }

                    filterSlider = RangeSlider(
                        Parameters.filterRange.start,
                        Parameters.filterRange.endInclusive,
                        Parameters.filterRange.start,
                        Parameters.filterRange.endInclusive,
                    )
                    filterSlider.hgrow = Priority.ALWAYS
                    add(filterSlider)
                    label {
                        val valueListener = ChangeListener<Number> { _, _, _ ->
                            text = "filter = " + filterSlider.range()
                            update()
                        }
                        filterSlider.lowValueProperty().addListener(valueListener)
                        filterSlider.highValueProperty().addListener(valueListener)
                    }
                }
            }
        }

        initialized = true
        updateSpectre()
        update()
    }

    private fun createGenerator(n: Int): Generator {
        return SignalFunctionGenerator(n, Parameters.createSignalFunction(spectre))
    }

    override fun update() {
        if (!initialized) return

        val n = n
        val range = jRange
        val signal = createGenerator(n).period()
        val fourier = if (isFast) FFT() else DPF.createFor(range, n)
        val spectre = fourier.spectre(range, signal)

        signals.data.setAll(signal.toSeries("Signal"))
        spectres.drawSpectre(spectre, false)

        reconstruct(fourier, spectre)
    }

    private fun reconstruct(fourier: Fourier, spectre: Spectre) {
        signals.data.addAll(
            fourier.signal(spectre, addInitial = true, withoutPhase = false).toSeries("Reconstructed"),
            fourier.signal(spectre, addInitial = true, withoutPhase = true).toSeries("Reconstructed without phase")
        )

        if (!filterEnabledCheckBox.isSelected) return

        val filter = SpectreFilter()
        val filtered = filter.filter(spectre, filterSlider.lowValue, filterSlider.highValue)
        signals.data.addAll(
            fourier.signal(
                filtered,
                addInitial = false,
                withoutPhase = false
            ).toSeries("Filtered")
        )

        val aj = Series<Number, Number>().apply { name = "Filtered amplitudes" }
        val phi = Series<Number, Number>().apply { name = "Filtered phases" }
        filtered.data.forEach {
            aj.data.add(XYChart.Data(it.f, it.a))
            phi.data.add(XYChart.Data(it.f, it.phi))
        }
        spectres.data.addAll(aj, phi)
    }

    private fun updateSpectre() {
        spectre = Spectre(
            n,
            listOf(Spectre.Part(0, 0.0, 0.0, 0.0, 0.0)).plus(
                (0 until 30).map {
                    Spectre.Part(
                        it + 1,
                        0.0,
                        0.0,
                        variant.amplitudes.random().toDouble(),
                        variant.phases.random().toDouble()
                    )
                }
            )
        )
        components.setAll(spectre.data)
    }

    override fun onChangeVariant() {
        updateSpectre()
    }

    class Parameters {
        companion object {

            val filterRange = 0.0..100.0

            val nQualifierRange = 0.0..4.0

            fun n(nQualifier: Double): Int {
                return (2.0.pow(nQualifier.roundToInt()) * 16).toInt()
            }

            fun createSignalFunction(
                spectre: Spectre
            ): SignalFunction {
                return { i, n ->
                    spectre.data.sumOf {
                        it.a * cos(2 * PI * i * it.f / n - it.phi)
                    }
                }
            }
        }
    }
}