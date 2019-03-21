package uni.cimbulka.network.simulator.core

import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.DefaultMonitor
import uni.cimbulka.network.simulator.core.models.Event

open class Simulator(val monitor: MonitorInterface = DefaultMonitor()) : AbstractSimulator() {
    override val events = ListQueue<EventInterface>()

    var time = 0.0
        private set

    fun start() {
        println("[$time] Starting simulation")

        while (true) {
            (events.removeFirst() as Event<*>?)?.let {
                time = it.time
                monitor.record(it)
                it(this)
            } ?: break
        }

        println("[$time] Simulation ended\n")
        monitor.printRecords()
    }
}