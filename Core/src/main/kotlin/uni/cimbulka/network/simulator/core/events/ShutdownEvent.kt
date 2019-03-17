package uni.cimbulka.network.simulator.core.events

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

class ShutdownEvent(override val time: Double) : Event<EventArgs>("Shutdown", EventArgs.empty) {
    override fun invoke(simulator: AbstractSimulator) {
        simulator.stop()
    }
}