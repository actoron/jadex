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

/**
 * This Router uses a simple flooding mechanism to route Pakets.
 * @author Julian Kalinowski
 *
 */
public class FloodingPacketRouter extends AbstractPacketRouter implements IPacketRouter {

	private static final RoutingType ROUTING_TYPE = RoutingType.Flooding;
	private Set<String> connectedDevices;

	private Set<String> reachableDevices;

	/**
	 * Constructor
	 */
	public FloodingPacketRouter() {
		super();
		connectedDevices = new HashSet<String>();
		reachableDevices = new HashSet<String>();
		
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
		if (pkt.getHopCount() < BTP2PConnector.MAXHOPS) {
			pkt.incHopCount();
			if (connectedDevices.contains(pkt.getDestination())) {
				// we have a direct connection to destination

				try {
					sendMessageToConnectedDevice(pkt, pkt.getDestination());
					future.setResult(BluetoothMessage.MESSAGE_SENT);
				} catch (MessageNotSendException e) {
					e.printStackTrace();
				}
			} else if (reachableDevices.contains(pkt.getDestination())){
				// broadcast to everyone except fromDevice
				for (String address : connectedDevices) {
					if (!address.equals(fromDevice)) {
						try {
							sendMessageToConnectedDevice(pkt, address);
							future.setResult(BluetoothMessage.MESSAGE_SENT);
						} catch (MessageNotSendException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				future.setException(new MessageNotSendException("Destination not reachable"));
			}
		} else {
			future.setException(new MessageNotSendException("MaxHops reached"));
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
			notifyReachableDevicesChanged();
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
		notifyConnectedDevicesChanged();
		broadcastRoutingInformation(createRoutingInformation());
	}

	private void broadcastRoutingInformation(RoutingInformation ri) {
		DataPacket pkt;
		try {
			for (String address : connectedDevices) {
				pkt = new DataPacket(address, ri.toByteArray(),
						DataPacket.TYPE_ROUTING_INFORMATION);
				try {
					sendMessageToConnectedDevice(pkt, address);
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
	public void forceBroadcast() {
		// no action required
	}

	@Override
	public void forceDeleteStale() {
		// no action required ??
	}

	@Override
	public void start() {
	}

}
