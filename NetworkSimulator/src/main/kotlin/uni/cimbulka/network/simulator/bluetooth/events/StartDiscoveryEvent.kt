package uni.cimbulka.network.simulator.bluetooth.events

import uni.cimbulka.network.simulator.Constants
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import kotlin.random.Random

data class StartDiscoveryEventArgs(val physicalLayer: PhysicalLayer, val adapter: BluetoothAdapter) : EventArgs()

class StartDiscoveryEvent(override val time: Double, args: StartDiscoveryEventArgs) :
        Event<StartDiscoveryEventArgs>("StartDiscovery", args) {

    override fun invoke(simulator: AbstractSimulator) {
        val (phy, adapter) = args

        val result = mutableListOf<Node>()
        phy.keys.forEach {
            if (phy.getDistance(adapter.node.id, it) <= Constants.Bluetooth.BLUETOOTH_RANGE) {
                phy[it]?.let { n ->
                    if (n.id != adapter.node.id)
                        result.add(n)
                }
            }
        }

        val delay = Random.nextDouble(Constants.Bluetooth.DISCOVERY_DELAY_RANGE.start, Constants.Bluetooth.DISCOVERY_DELAY_RANGE.endInclusive)
        simulator.insert(EndDiscoveryEvent(time + delay, EndDiscoveryEventArgs(adapter, result)))
    }
}