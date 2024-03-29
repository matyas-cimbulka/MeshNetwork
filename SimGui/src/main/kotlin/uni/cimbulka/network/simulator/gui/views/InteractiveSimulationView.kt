package uni.cimbulka.network.simulator.gui.views

import javafx.scene.layout.AnchorPane
import javafx.util.converter.DoubleStringConverter
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.InteractiveSimulationController

class InteractiveSimulationView : View("Interactive simulation") {
    private val controller: InteractiveSimulationController by inject()
    private val graphView: GraphView by inject()

    override val root = borderpane {
        prefWidth = 1024.0
        prefHeight = 640.0

        top = hbox {
            button {
                textProperty().bind(controller.startButtonTextProperty())
                action(controller::handleStartButton)
            }
            label("Time: ")
            label {
                bind(controller.timeProperty(), converter = DoubleStringConverter())
            }
        }

        left = vbox {
            button("Send message") { action(controller::sendMessage) }
        }

        center = anchorpane {
            add(graphView)
            AnchorPane.setLeftAnchor(graphView.root, 0.0)
            AnchorPane.setTopAnchor(graphView.root, 0.0)
            AnchorPane.setRightAnchor(graphView.root, 0.0)
            AnchorPane.setBottomAnchor(graphView.root, 0.0)
        }
    }

    override fun onDock() {
        graphView.fireEvents = true
    }
}
