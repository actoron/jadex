package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.service.IFuture;

import java.util.Set;

/**
 * Packet Router Interface
 * 
 * @author Julian Kalinowski
 * 
 */
public interface IPacketRouter {

	/**
	 * Listener Interface
	 * 
	 * @author Julian Kalinowski
	 */
	interface RoutingEntriesChangeListener {
		/**
		 * Called when a direct connection was made or removed
		 */
		void connectedDevicesChanged();

		/**
		 * Called when an indirect connection was made or removed
		 */
		void reachableDevicesChanged();
	}

	/**
	 * Adds a Listener to this Router.
	 * 
	 * @param l
	 */
	void addRoutingEntriesChangedListener(RoutingEntriesChangeListener l);

	/**
	 * @param l
	 * @return
	 */
	boolean removeReachableDevicesChangeListener(RoutingEntriesChangeListener l);

	/**
	 * Sets the IPacketSender instance, which will be used to send Packets to
	 * directly connected Devices.
	 * 
	 * @param sender {@link IPacketSender}
	 */
	void setPacketSender(IPacketSender sender);

	/**
	 * The main Routing-Method, called to route a Packet to a specific destination.
	 * @param packet The {@link DataPacket} to route, contains information about the target
	 * @param fromDevice the Address of the Device by which this Packet was received (to avoid loops)
	 * @return {@link IFuture}
	 */
	IFuture routePacket(DataPacket packet, String fromDevice);

	/**
	 * Adds a directly connected Device 
	 * @param device
	 */
	void addConnectedDevice(String device);

	/**
	 * Removes a directly connected Device
	 * @param device
	 */
	void removeConnectedDevice(String device);

	/**
	 * Updates the Routing Table with a newly received {@link RoutingInformation}
	 * @param ri the {@link RoutingInformation}
	 */
	void updateRoutingInformation(RoutingInformation ri);

	/**
	 * Returns the Local Adapter Address
	 * @return String
	 */
	String getOwnAddress();

	/**
	 * Sets the Local Adapter Address
	 * @param address String
	 */
	void setOwnAddress(String address);

	/**
	 * Starts the PacketRouter
	 */
	void start();

	/**
	 * 
	 * @return all entries in the routing table that are reachable via more than
	 *         zero hops (e.g. not directly connected)
	 */

	Set<String> getReachableDeviceAddresses();

	/**
	 * 
	 * @return all entries in the routing table that are reachable via zero hops
	 *         = directly connected
	 */
	Set<String> getConnectedDeviceAddresses();

	/**
	 * Forces this router to broadcast it's table.
	 */
	void forceBroadcast();

	/**
	 * Forces this router to delete stale routing entries.
	 */
	void forceDeleteStale();
}