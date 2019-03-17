package uni.cimbulka.network

import kotlinx.coroutines.Job
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.events.StartDiscoveryEvent
import uni.cimbulka.network.events.StartDiscoveryEventArgs
import uni.cimbulka.network.listeners.CommServiceListener
import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Route
import uni.cimbulka.network.models.RouteSegment
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.packets.PacketSender
import uni.cimbulka.network.packets.RouteDiscoveryRequest
import uni.cimbulka.network.simulator.core.Simulator
import java.util.*

class NetworkController(friendlyName: String, private val simulator: Simulator) {

    private lateinit var mainJob: Job
    internal val networkSession = NetworkSession()

    var networkCallbacks: NetworkCallbacks?
        get() = networkSession.networkCallbacks
        set(value) { networkSession.networkCallbacks = value }

    val localDevice: Device
        get() = networkSession.localDevice

    init {
        networkSession.controller = this
        networkSession.localDevice = Device(UUID.randomUUID(), friendlyName)
        networkSession.simulator = simulator
        networkSession.services.forEach {
            it.serviceCallbacks = CommServiceListener(this)
        }
    }

    internal fun updateNetwork(data: UpdateData) {
        if (data.updates.isNotEmpty()) {
            val packet = BroadcastPacket(networkSession.incrementPacketCount(), localDevice, data)
            PacketSender.send(packet, networkSession)
        }
    }

    fun getDevicesInNetwork(): List<Device> = networkSession.networkGraph.devices.filter { it != networkSession.localDevice }

    fun start() {
        startMainJob()
    }

    fun stop() {
        stopService()

        networkSession.mainJob = false
        networkSession.nextDiscoveryEvent?.let {
            simulator.cancel(it)
        }
    }

    fun send(packet: BroadcastPacket) {
        PacketSender.send(packet, networkSession)
    }

    fun send(packet: DataPacket) {
        PacketSender.send(packet, networkSession)
    }

    fun addCommService(service: CommService) {
        service.serviceCallbacks = CommServiceListener(this)
        networkSession.services.add(service)
    }

    internal fun startService() {
        networkSession.services.forEach { it.startService(); }
    }

    internal fun stopService() {
        networkSession.services.forEach { it.stopService(); }
    }

    private fun startMainJob() {
        simulator.insert(StartDiscoveryEvent(simulator.time, StartDiscoveryEventArgs(networkSession)))
        networkSession.mainJob = true
    }

    private fun scan() {
        networkSession.services.forEach { it.startDiscovery(networkSession.isInNetwork); }
    }

    internal fun discoverRoute(target: Device) {
        discoverRoute(RouteDiscoveryRequest(
                networkSession.incrementPacketCount(), networkSession.localDevice, Date().time,
                requester = networkSession.localDevice, target = target, route = Route()
        ))
    }

    internal fun discoverRoute(request: RouteDiscoveryRequest) {
        networkSession.networkGraph.borderNodes.forEach {
            val p = request.copy().apply {
                recipient = it
                route?.segments?.add(RouteSegment(networkSession.localDevice, it))
            }
            PacketSender.send(p, networkSession)
        }
    }
}