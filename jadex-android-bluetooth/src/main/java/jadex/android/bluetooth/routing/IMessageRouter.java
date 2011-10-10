package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.DataPacket;
import jadex.android.bluetooth.domain.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.service.IFuture;

import java.util.Set;



public interface IMessageRouter {
	
	interface ReachableDevicesChangeListener {
		void reachableDevicesChanged();
	}
	
	void addReachableDevicesChangeListener(ReachableDevicesChangeListener l);
	
	boolean removeReachableDevicesChangeListener(ReachableDevicesChangeListener l);
	
	void setPacketSender(IMessageSender sender);

	IFuture routePacket(DataPacket packet, String fromDevice);

	void addConnectedDevice(String device);

	void removeConnectedDevice(String device);

	void updateRoutingInformation(RoutingInformation ri);

	Set<String> getReachableDeviceAddresses();

	Set<String> getConnectedDeviceAddresses();
}
