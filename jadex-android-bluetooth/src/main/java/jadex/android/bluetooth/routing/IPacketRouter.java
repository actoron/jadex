package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.service.IFuture;

import java.util.Set;

public interface IPacketRouter {
	
	interface ReachableDevicesChangeListener {
		void reachableDevicesChanged();
	}
	
	void addReachableDevicesChangeListener(ReachableDevicesChangeListener l);
	
	boolean removeReachableDevicesChangeListener(ReachableDevicesChangeListener l);
	
	void setPacketSender(IPacketSender sender);

	IFuture routePacket(DataPacket packet, String fromDevice);

	void addConnectedDevice(String device);

	void removeConnectedDevice(String device);

	void updateRoutingInformation(RoutingInformation ri);

	/**
	 * 
	 * @return all entries in the routing table that are reachable via more than
	 *         zero hops (e.g. not directly connected)
	 */

	Set<String> getReachableDeviceAddresses();

	Set<String> getConnectedDeviceAddresses();
}