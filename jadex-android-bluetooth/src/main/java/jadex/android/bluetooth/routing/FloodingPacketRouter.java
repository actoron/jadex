package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.connection.BTP2PConnector;
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.exceptions.MessageNotSendException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder;
import jadex.android.bluetooth.message.MessageProtos.RoutingTable;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.message.MessageProtos.RoutingType;
import jadex.android.bluetooth.service.Future;
import jadex.android.bluetooth.service.IFuture;
import jadex.android.bluetooth.util.Helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

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

				try {
					sender.sendMessageToConnectedDevice(pkt, pkt.Dest);
				} catch (MessageNotSendException e) {
					e.printStackTrace();
				}
			} else {
				// broadcast to everyone except fromDevice
				for (String address : connectedDevices) {
					if (!address.equals(fromDevice)) {
						try {
							sender.sendMessageToConnectedDevice(pkt, address);
						} catch (MessageNotSendException e) {
							e.printStackTrace();
						}
					}
				}
				future.setResult(BluetoothMessage.MESSAGE_SENT);
			}
		} else {
			// maxhops reached.
		}
		return future;
	}

	@Override
	public void updateRoutingInformation(RoutingInformation ri) {
		boolean changed = false;
		if (ri.getType().equals(ROUTING_TYPE)) {
			RoutingTable routingTable = ri.getRoutingTable();
			List<RoutingTableEntry> routingEntries = routingTable
					.getEntryList();

			for (RoutingTableEntry routingTableEntry : routingEntries) {
				String address = routingTableEntry.getDestination();
				int numHops = routingTableEntry.getNumHops();
				if (!address.equals(ownAddress)
						&& (!connectedDevices.contains(address))) {
					if (numHops == Integer.MAX_VALUE) {
						boolean remove = reachableDevices.remove(address);
						changed = remove ? true : changed;
					} else {
						boolean add = reachableDevices.add(address);
						changed = add ? true : changed;
					}
				}
			}
		}
		if (changed) {
			// proximityDevicesChanged();
			broadcastRoutingInformation(createRoutingInformation());
		}
	}

	private RoutingInformation createRoutingInformation(
			String... unreachableDevices) {
		Builder riBuilder = MessageProtos.RoutingInformation.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTable.Builder rtBuilder = MessageProtos.RoutingTable
				.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry.Builder entryBuilder = MessageProtos.RoutingTableEntry
				.newBuilder();

		for (String s : reachableDevices) {
			entryBuilder.setDestination(s);
			entryBuilder.setNumHops(1);
			rtBuilder.addEntry(entryBuilder.build());
		}

		for (String address : connectedDevices) {
			entryBuilder.setDestination(address);
			entryBuilder.setNumHops(0);
			rtBuilder.addEntry(entryBuilder.build());
		}

		for (String address : unreachableDevices) {
			entryBuilder.setDestination(address);
			entryBuilder.setNumHops(Integer.MAX_VALUE);
			rtBuilder.addEntry(entryBuilder.build());
		}

		riBuilder.setType(ROUTING_TYPE);
		riBuilder.setRoutingTable(rtBuilder);
		return riBuilder.build();

	}

	@Override
	public void addConnectedDevice(String device) {
		connectedDevices.add(device);
		broadcastRoutingInformation(createRoutingInformation());
	}

	private void broadcastRoutingInformation(RoutingInformation ri) {
		notifyListeners();

		DataPacket pkt;
		try {
			pkt = new DataPacket("", ri.toByteArray(),
					DataPacket.TYPE_ROUTING_INFORMATION);
			for (String address : connectedDevices) {
				pkt.Dest = address;
				try {
					sender.sendMessageToConnectedDevice(pkt, address);
				} catch (MessageNotSendException e) {
					e.printStackTrace();
				}
			}
		} catch (MessageConvertException e1) {
			e1.logThisException();
		}
	}

	@Override
	public void removeConnectedDevice(String device) {
		boolean remove = connectedDevices.remove(device);
		if (remove) {
			broadcastRoutingInformation(createRoutingInformation(device));
		}
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
