package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.connection.BTP2PConnector;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder;
import jadex.android.bluetooth.message.MessageProtos.RoutingTable;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.message.MessageProtos.RoutingType;
import jadex.android.bluetooth.service.Future;
import jadex.android.bluetooth.service.IFuture;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FloodingPacketRouter implements IPacketRouter {

	private static final RoutingType ROUTING_TYPE = RoutingType.Flooding;
	private Set<String> connectedDevices;
	private IPacketSender sender;

	private Set<String> reachableDevices;
	private String ownAddress;
	private Set<ReachableDevicesChangeListener> listeners;

	public FloodingPacketRouter(String ownAddress) {
		this.ownAddress = ownAddress;
		connectedDevices = new HashSet<String>();
		reachableDevices = new HashSet<String>();
		listeners = new HashSet<IPacketRouter.ReachableDevicesChangeListener>();
	}

	@Override
	public void setPacketSender(IPacketSender sender) {
		this.sender = sender;
	}

	/**
	 * Routes the Packet to all connected Devices except the device specified in
	 * fromDevice.
	 * 
	 * @param pkt
	 *            Packet to route
	 * @param fromDevice
	 *            don't route Packet to this device
	 */
	@Override
	public IFuture routePacket(DataPacket pkt, String fromDevice) {
		Future future = new Future();
		if (pkt.HopCount < BTP2PConnector.MAXHOPS) {
			pkt.HopCount++;
			if (connectedDevices.contains(pkt.Dest)) {
				// we have a direct connection to destination

				sender.sendMessageToConnectedDevice(pkt, pkt.Dest);
			} else {
				// broadcast to everyone except fromDevice
				for (String address : connectedDevices) {
					if (!address.equals(fromDevice)) {
						sender.sendMessageToConnectedDevice(pkt, address);
					}
				}
				future.setResult(BTP2PConnector.MESSAGE_SENT);
			}
		} else {
			// maxhops reached.
		}
		return future;
	}

	@Override
	public void updateRoutingInformation(RoutingInformation ri) {
		boolean changed = false;
		Set<String> deviceList = Collections.emptySet();
		if (ri.getType().equals(ROUTING_TYPE)) {
			RoutingTable routingTable = ri.getRoutingTable();
			List<RoutingTableEntry> routingEntries = routingTable
					.getEntryList();

			deviceList = new HashSet<String>(routingEntries.size());
			for (RoutingTableEntry routingTableEntry : routingEntries) {
				String address = routingTableEntry.getDestination();
				if (!address.equals(ownAddress)
						&& (!connectedDevices.contains(address))) {
					boolean add = reachableDevices.add(address);
					if (add) {
						changed = true;
					}
				}
			}
		}
		if (changed) {
			// proximityDevicesChanged();
			broadcastRoutingInformation();
		}
	}

	private RoutingInformation createRoutingInformation() {
		Builder riBuilder = MessageProtos.RoutingInformation.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTable.Builder rtBuilder = MessageProtos.RoutingTable.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry.Builder entryBuilder = MessageProtos.RoutingTableEntry.newBuilder();
		
		for (String s : reachableDevices) {
			entryBuilder.setDestination(s);
			rtBuilder.addEntry(entryBuilder.build());
		}

		for (String address : connectedDevices) {
			entryBuilder.setDestination(address);
			rtBuilder.addEntry(entryBuilder.build());
		}
		
		riBuilder.setType(ROUTING_TYPE);
		riBuilder.setRoutingTable(rtBuilder);
		return riBuilder.build();

	}

	@Override
	public void addConnectedDevice(String device) {
		connectedDevices.add(device);
		broadcastRoutingInformation();
	}

	private void broadcastRoutingInformation() {
		notifyListeners();

		RoutingInformation ri = createRoutingInformation();
		DataPacket pkt = new DataPacket("", ri.toByteArray(),
				DataPacket.TYPE_ROUTING_INFORMATION);

		for (String address : connectedDevices) {
			pkt.Dest = address;
			sender.sendMessageToConnectedDevice(pkt, address);
		}
	}

	@Override
	public void removeConnectedDevice(String device) {
		connectedDevices.remove(device);
		broadcastRoutingInformation();
	}

	@Override
	public Set<String> getReachableDeviceAddresses() {
		return reachableDevices;
	}

	@Override
	public Set<String> getConnectedDeviceAddresses() {
		return connectedDevices;
	}

	@Override
	public void addReachableDevicesChangeListener(
			ReachableDevicesChangeListener l) {
		listeners.add(l);
	}

	@Override
	public boolean removeReachableDevicesChangeListener(
			ReachableDevicesChangeListener l) {
		return listeners.remove(l);
	}

	private void notifyListeners() {
		for (ReachableDevicesChangeListener l : listeners) {
			l.reachableDevicesChanged();
		}
	}

	@Override
	public String getOwnAddress() {
		return ownAddress;
	}

	@Override
	public void setOwnAddress(String address) {
		ownAddress = address;
	}

}
