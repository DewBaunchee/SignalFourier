package view.task

import domain.Variant
import domain.signal.Signal
import domain.signal.Spectre
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import org.controlsfx.control.RangeSlider
import tornadofx.*
import kotlin.math.roundToInt

abstract class Task(protected var variant: Variant) : StackPane() {

    protected var initialized = false

    fun changeVariant(variant: Variant) {
        this.variant = variant
        onChangeVariant()
        update()
    }

    open fun onChangeVariant() {

    }

    protected abstract fun update()

    protected fun Parent.chart(xAxisTitle: String): XYChart<Number, Number> {
        return linechart("", NumberAxis(), NumberAxis()) {
            xAxis.label = xAxisTitle
            createSymbols = false
            animated = false
            vgrow = Priority.ALWAYS
        }
    }

    protected fun XYChart<Number, Number>.drawSpectre(spectre: Spectre, full: Boolean = true) {
        val aj = XYChart.Series<Number, Number>().also { it.name = "Aj" }
        val phi = XYChart.Series<Number, Number>().also { it.name = "phi" }

        spectre.data.forEach {
            aj.data.add(XYChart.Data(it.f, it.a))
            phi.data.add(XYChart.Data(it.f, it.phi))
        }
        data.setAll(aj, phi)

        if (!full) return

        val acj = XYChart.Series<Number, Number>().also { it.name = "Acj" }
        val asj = XYChart.Series<Number, Number>().also { it.name = "Asj" }

        spectre.data.forEach {
            acj.data.add(XYChart.Data(it.f, it.aC))
            asj.data.add(XYChart.Data(it.f, it.aS))
        }
        data.setAll(acj, asj)
    }

    protected fun Signal.toSeries(name: String): XYChart.Series<Number, Number> {
        return XYChart.Series<Number, Number>().also {
            it.name = name
            it.data.setAll(mapToChart())
        }
    }

    private fun Signal.mapToChart(): List<XYChart.Data<Number, Number>> {
        return data.map { XYChart.Data(it.i, it.value) }
    }

    protected fun RangeSlider.range(): IntRange {
        return lowValue.roundToInt()..highValue.roundToInt()
    }

    protected fun <T> ObservableValue<T>.listenValue(listener: ChangeListener<T>) {
        listener.changed(this, null, value)
        addListener(listener)
    }
}