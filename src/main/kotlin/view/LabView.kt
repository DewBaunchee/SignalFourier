package view

import domain.Variant
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import tornadofx.*
import view.task.Task
import view.task.harmonic.HarmonicTask
import view.task.polyharmonic.PolyharmonicTask
import view.task.smooth.SmoothTask


class LabView : View("COS 2") {

    override fun onDock() {
        super.onDock()
        primaryStage.isMaximized = true
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED) {
            when (it.code) {
                KeyCode.DIGIT1 -> changeVariant(1)
                KeyCode.DIGIT2 -> changeVariant(2)
                KeyCode.DIGIT3 -> changeVariant(3)
                KeyCode.DIGIT4 -> changeVariant(4)
                KeyCode.DIGIT5 -> changeVariant(5)
                KeyCode.DIGIT6 -> changeVariant(6)
                else -> {}
            }
        }
    }

    private lateinit var container: StackPane
    private var variant = Variant.of(6)
    private var task: Task = HarmonicTask(variant)
        set(value) {
            field = value
            container.children.setAll(value)
        }

    override var root = vbox {
        standardSpacing()

        hbox {
            standardSpacing()

            style {
                fontSize = 10.px
            }

            button("Harmonic") { onAction = EventHandler { task = HarmonicTask(variant) } }
            button("Polyharmonic") { onAction = EventHandler { task = PolyharmonicTask(variant) } }
            button("Smoothing") { onAction = EventHandler { task = SmoothTask(variant) } }
        }

        container = stackpane {
            hgrow = Priority.ALWAYS
            children.setAll(task)
        }
    }

    private fun changeVariant(variant: Int) {
        if (variant < 1 || variant > 6) return
        task.changeVariant(Variant.of(variant))
    }
}

fun VBox.standardSpacing() {
    paddingAll = 10
    spacing = 10.0
}

fun HBox.standardSpacing() {
    paddingAll = 10
    spacing = 10.0
}