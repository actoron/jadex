package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.BTP2PConnector;
import jadex.android.bluetooth.DataPacket;
import jadex.android.bluetooth.IConnection;
import jadex.android.bluetooth.domain.MessageProtos;
import jadex.android.bluetooth.domain.MessageProtos.DeviceList;
import jadex.android.bluetooth.domain.MessageProtos.DeviceList.Builder;
import jadex.android.bluetooth.routing.IMessageRouter.ReachableDevicesChangeListener;
import jadex.android.bluetooth.service.Future;
import jadex.android.bluetooth.service.IFuture;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;


import android.bluetooth.BluetoothDevice;

public class FloodingPacketRouter implements IMessageRouter {

	private Set<String> connectedDevices;
	private IMessageSender sender;

	private Set<String> reachableDevices;
	private String ownAddress;
	private Set<ReachableDevicesChangeListener> listeners;
	
	public FloodingPacketRouter(String ownAddress) {
		this.ownAddress = ownAddress;
		connectedDevices = new HashSet<String>();
		reachableDevices = new HashSet<String>();
		listeners = new HashSet<IMessageRouter.ReachableDevicesChangeListener>();
	}

	@Override
	public void setPacketSender(IMessageSender sender) {
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
	public void updateRoutingInformation(IRoutingInformation ri) {
		boolean changed = false;
		List<String> deviceList = Collections.emptyList();
		if (ri.getRoutingType()
				.equals(IRoutingInformation.RoutingType.Flooding)) {
			deviceList = ri.getReachableDeviceList();
		}
		for (String address : deviceList) {
			if (!address.equals(ownAddress) && (!connectedDevices.contains(address))) {
				boolean add = reachableDevices.add(address);
				if (add) {
					changed = true;
				}
			}
		}
		if (changed) {
			// proximityDevicesChanged();
			broadcastRoutingInformation();
		}
	}

	private DeviceList createReachableDeviceList() {
		Builder newBuilder = MessageProtos.DeviceList.newBuilder();
		for (String s : reachableDevices) {
			newBuilder.addDevice(s);
		}

		for (String address : connectedDevices) {
			newBuilder.addDevice(address);
		}

		return newBuilder.build();
	}

	@Override
	public void addConnectedDevice(String device) {
		connectedDevices.add(device);
		broadcastRoutingInformation();
	}

	private void broadcastRoutingInformation() {
		notifyListeners();
		
		DeviceList list = createReachableDeviceList();
		DataPacket pkt = new DataPacket("", list.toByteArray(),
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

}
