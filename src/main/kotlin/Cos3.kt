import javafx.application.Application
import tornadofx.*
import view.LabView

class Cos3 : App(LabView::class)

fun main(args: Array<String>) {
    Application.launch(Cos3::class.java, *args)
}