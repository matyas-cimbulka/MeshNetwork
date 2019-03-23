package uni.cimbulka.network

import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BasePacket
import java.util.*

class NetworkSession {

    internal var networkGraph: NetworkGraph = NetworkGraph(this)
    val neighbours = mutableMapOf<String, Device>()
    val knownDevices = mutableMapOf<String, Device>()
    val services: MutableList<CommService> = mutableListOf()
    var networkCallbacks: NetworkCallbacks? = null
    val longDistanceVectors: MutableMap<Device, MutableCollection<Device>> = mutableMapOf()
    val allDevices: MutableList<Device> = mutableListOf()
    lateinit var localDevice: Device
    private var packetCount = 0

    internal var processedUpdates = mutableMapOf<Long, Update>()

    val processedPackets = mutableListOf<BasePacket>()
        get() {
            field.removeIf { Math.abs(Date().time - it.timestamp) > 5 * 60 * 1000 }
            return field
        }

    var isInNetwork = false
        set(value) {
            localDevice.isInNetwork = value
            field = value
        }

    internal var routingTable = RoutingTable(emptyMap())
        get() = networkGraph.calcRoutingTable()
        private set

    fun incrementPacketCount(): Int {
        packetCount++
        return packetCount
    }

    fun startServices() {
        services.forEach { it.startService() }
    }

    //- Simulation stuff ----------------------------------------------------------------------------
    var mainJob = false
}