package by.varyvoda.fourier

import javafx.application.Application
import tornadofx.*
import by.varyvoda.fourier.view.LabView

class FourierApp : App(LabView::class)

fun main(args: Array<String>) {
    Application.launch(FourierApp::class.java, *args)
}