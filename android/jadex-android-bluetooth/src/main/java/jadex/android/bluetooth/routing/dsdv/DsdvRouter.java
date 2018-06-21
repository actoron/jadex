package jadex.android.bluetooth.routing.dsdv;

import jadex.android.bluetooth.exceptions.MessageNotSendException;
import jadex.android.bluetooth.exceptions.RoutingTableException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.message.MessageProtos.RoutingTable;
import jadex.android.bluetooth.message.MessageProtos.RoutingTable.Builder;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.message.MessageProtos.RoutingType;
import jadex.android.bluetooth.routing.AbstractPacketRouter;
import jadex.android.bluetooth.routing.IPacketRouter;
import jadex.android.bluetooth.routing.IPacketSender;
import jadex.android.bluetooth.routing.IPacketRouter.RoutingEntriesChangeListener;
import jadex.android.bluetooth.routing.dsdv.info.ConfigInfo;
import jadex.android.bluetooth.routing.dsdv.info.CurrentInfo;
import jadex.android.bluetooth.routing.dsdv.minders.BroadcastMinder;
import jadex.android.bluetooth.routing.dsdv.minders.PeriodicBroadcastMinder;
import jadex.android.bluetooth.routing.dsdv.minders.RouteSender;
import jadex.android.bluetooth.routing.dsdv.net.LocalRoutingTable;
import jadex.android.bluetooth.routing.dsdv.net.RoutingTableEntryWrapper;
import jadex.android.bluetooth.service.Future;
import jadex.android.bluetooth.service.IFuture;
import jadex.android.bluetooth.util.Helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

/**
 * Implementation of the DSDV routing algorithm.
 * Includes Code from http://code.google.com/p/beddernet/
 * @author 8kalinow
 */
public class DsdvRouter extends AbstractPacketRouter implements IPacketRouter {

	private static final RoutingType ROUTING_TYPE = RoutingType.DSDV;

	private String TAG = Helper.LOG_TAG;

	private LocalRoutingTable routeTable;
	private BroadcastMinder broadcaster;
	private PeriodicBroadcastMinder periodicBroadcaster;
	
	/**
	 * manages a route dampening time for new or deleted routes
	 */
	private RouteSender routeSender;

	/**
	 * Class constructor
	 */
	public DsdvRouter() {
		super();
	}
	
	/**
	 * starts the Router.
	 * Needs ownAddress set before starting.
	 */
	public void start() {
		initRoutingTable();
		broadcaster = new BroadcastMinder(this, routeTable);
		periodicBroadcaster = new PeriodicBroadcastMinder(this, routeTable);
		broadcaster.start();
		periodicBroadcaster.start();
	}

	/**
	 * Method to stop the protocol handler. Does this by stopping all the
	 * threads that were started start function and cleaning up the routing
	 * environment
	 */
	public synchronized void stopApplication() {

		// stop broadcasting thread
		broadcaster.abort();

		// stop the periodic broadcaster
		periodicBroadcaster.abort();

		// start the route deleter
		// staleRouteDeleter.stop();
	}

	/**
	 * Init the environment, for new user, broadcast your routing table i.e. the
	 * one line table containing yourself
	 */
	private void initRoutingTable() {
		Log.i(TAG, "initializing routing");
		String dest = getOwnAddress();
		String next_hop = dest;
		int hops = 0;
		RoutingTableEntryWrapper rte = new RoutingTableEntryWrapper(dest,
				next_hop, hops, CurrentInfo.incrementOwnSeqNum());

		routeTable = new LocalRoutingTable(ownAddress);
		routeTable.addRoutingEntry(rte);
		// Announce arrival on the network
		broadcastRouteTableMessage();
	}

	/**
	 * The route is down. Reestablish new routes
	 * 
	 * @param toNetworkAddresses
	 *            the addresses to send "rounte down" messages to
	 */
	public void BroadcastRouteDown(String[] toNetworkAddresses) {
		for (String l : toNetworkAddresses) {
			broadcastRouteDown(l);
		}
	}

	/**
	 * Broadcasts a route broadcast packet
	 * 
	 * @param routes
	 *            The route to broadcast
	 * @param routeDown
	 *            indicates whether the route(s) are down
	 */
	private void broadcastRouteBroadcastPacket(
			Vector<RoutingTableEntryWrapper> routes, boolean routeDown) {
		Vector<String> neighbors = routeTable.getNeighborAddresses();
		if (neighbors != null) {
			for (int i = 0; i < neighbors.size(); i++) {
				String dest = neighbors.get(i);
				try {
					Builder rtBuilder = jadex.android.bluetooth.message.MessageProtos.RoutingTable
							.newBuilder();
					for (RoutingTableEntryWrapper rtew : routes) {
						rtBuilder.addEntry(rtew.build());
					}

					jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder riBuilder = RoutingInformation
							.newBuilder();
					riBuilder.setRoutingTable(rtBuilder);
					riBuilder.setRouteDownInformation(routeDown);
					riBuilder.setType(RoutingType.DSDV);
					riBuilder.setFromAddress(getOwnAddress());

					DataPacket packet = new DataPacket(dest, riBuilder.build()
							.toByteArray(), DataPacket.TYPE_ROUTING_INFORMATION);
					sendMessageToConnectedDevice(packet, dest);
				} catch (Exception exception) {
					Log.e(TAG, "Error in BroadcastRouteBroadcastPacket",
							exception);
					broadcastRouteDown(dest);
				}
			}
		}
	}

	//
	/**
	 * Updates the seq num of the route to an odd number and sends that route
	 * immediatly to all neighbors. Should send also an updated route concerning
	 * all routes that have dest as next hop, update those to odd num as well
	 * and send
	 * 
	 * @param dest
	 *            the destination that is down
	 */

	public void broadcastRouteDown(String dest) {
		RoutingTableEntryWrapper rte = routeTable.getRoutingEntry(dest);
		if (rte != null) {
			// get all routes concerning dest, set as invalid
			Vector<RoutingTableEntryWrapper> route = routeTable
					.getRoutesAsVectorInvalid(dest);

			broadcastRouteBroadcastPacket(route, true);
		}
	}

	/**
	 * Sends all active routes (routes with even seq nums) to all neighbors
	 */
	public void broadcastRouteTableMessage() {
		Vector<RoutingTableEntryWrapper> routes = routeTable
				.getAllRoutesAsVector();
		broadcastRouteBroadcastPacket(routes, false);
	}

	/**
	 * Sends the supplied routes to all neighbors, used to send incremental
	 * updates as well as immediate changes
	 * 
	 * @param Vector<RoutingTableEntryWrapper> routes
	 *            routes The vector of RoutingTableEntryWrapper objects to be
	 *            sent
	 */
	public void BroadcastRouteTableMessageInstant(
			Vector<RoutingTableEntryWrapper> routes) {
		broadcastRouteBroadcastPacket(routes, false);
	}

	/**
	 * Processes a Route Down message, this is sent when sending a packet
	 * detects a broken link/route
	 */
	public void processDSDVRouteDownBroadcast(RoutingInformation rInfo) {

		RoutingTable routingTable = rInfo.getRoutingTable();
		List<RoutingTableEntry> routes = routingTable.getEntryList();
		// log.out("Processing down message :"+rBroad.routes.size());
		boolean changedReachable = false;
		boolean changedConnected= false;
		for (int i = 0; i < routes.size(); i++) {
			RoutingTableEntryWrapper rte = new RoutingTableEntryWrapper(
					routes.get(i));
			// check if route exists
			RoutingTableEntryWrapper existing = routeTable.getRoutingEntry(rte
					.getDestination());
			// if I use the rte then increase the hop count by one
			rte.setNumHops(rte.getNumHops() + 1);

			if (existing != null && existing.getSeqNum() < rte.getSeqNum()
					&& rInfo.getFromAddress().equals(existing.getNextHop())) {
				routeTable.addRoutingEntry(rte);
				if (rte.getNumHops() < 2){
					changedConnected = true;
				} else {
					changedReachable = true;
				}
			}
		}
		if (changedReachable) {
			notifyReachableDevicesChanged();
		}
		if (changedConnected) {
			notifyConnectedDevicesChanged();
		}
	}

	/**
	 * DSDVBroadcast is a standard broadcast message used to send routing table
	 * information Processing this message requires going through all routes
	 * supplied in the package and analyze whether they are better than the
	 * known routes.
	 * 
	 * @param rBroad
	 *            A broadcast message received and forwarded
	 */

	public void processDSDVMsgBroadcast(RoutingInformation rBroad) {
		try {
			boolean reBroadcast = false;
			boolean changedConnected = false;
			boolean changedReachable = false;
			List<RoutingTableEntry> routes = rBroad.getRoutingTable()
					.getEntryList();
			// System.out.println("Processing broadcast. :"+rBroad.routes.size());
			for (int i = 0; i < routes.size(); i++) {
				RoutingTableEntryWrapper rte = new RoutingTableEntryWrapper(
						routes.get(i));
				// check if route exists
				RoutingTableEntryWrapper existing = routeTable
						.getRoutingEntry(rte.getDestination());
				// if I use the rte then increase the hop count by one
				rte.setNumHops(rte.getNumHops() + 1);
				if (rBroad.getFromAddress() == null) {
					throw new RoutingTableException("No 'from' address specified for Routing Entry: " + rte.getDestination());
				}
				rte.setNextHop(rBroad.getFromAddress());
				// System.out.println("**** Got from " +
				// rBroad.fromNetAddr.getAddressAsString()+" to "+rte.getDestination().getAddressAsString()
				// + " with sewnum "+rte.getSeqNum());
				// assume new node in the network
				if (existing == null) {// || rte.getSeqNum() == 0) {
					// System.out.println("Added a route.. not in table");
					// routeSender.reset();
					rte.setRouteChanged(true); // mark as sent now
					routeTable.addRoutingEntry(rte);
					if (rte.getNumHops() < 2){
						changedConnected = true;
					} else {
						changedReachable = true;
					}
					reBroadcast = true;
				} else // compare the existing route with the new one
				{
					// System.out.println("Route exists");
					// If the dest is myself and the sequence num is higher and
					// odd then increment own seq num to one higher then the one
					// sent
					if (rte.getDestination().equals(getOwnAddress())) {
						if (rte.getSeqNum() % 2 == 1
							&& rte.getSeqNum() > CurrentInfo.lastSeqNum) {
							// only care if seqNr was higher
							CurrentInfo.setOwnSeqNum(rte.getSeqNum() + 1);
							rte.setSeqNum(CurrentInfo.lastSeqNum);
							rte.setNextHop(getOwnAddress());
							rte.setNumHops(0);
							rte.setRouteChanged(true);
							routeTable.addRoutingEntry(rte);
							if (rte.getNumHops() < 2){
								changedConnected = true;
							} else {
								changedReachable = true;
							}
							reBroadcast = true;
						}
					} else {
						if (existing.getSeqNum() < rte.getSeqNum()) {
							// System.out.println("Got from " +
							// rBroad.fromNetAddr.getAddressAsString()+" to "+rte.getDestination().getAddressAsString()
							// +
							// " with sewnum "+rte.getSeqNum()+" existing seq is lower");
							// if there is a newer Seq num and the node I get the
							// message
							// from is the next hop for the destination then store
							// route
							if (rte.getSeqNum() % 2 == 1
									&& rBroad.getFromAddress() == existing
											.getNextHop()) {
								// System.out.println("Got from " +
								// rBroad.fromNetAddr.getAddressAsString()+" to "+rte.getDestination().getAddressAsString()
								// +
								// " with sewnum "+rte.getSeqNum()+" route is odd num");
								rte.setRouteChanged(true);
								routeTable.addRoutingEntry(rte);
								if (rte.getNumHops() < 2){
									changedConnected = true;
								} else {
									changedReachable = true;
								}
								reBroadcast = true;
	
							} else if (rte.getSeqNum() % 2 == 0) {
								// System.out.println("Got from " +
								// rBroad.fromNetAddr.getAddressAsString()+" to "+rte.getDestination().getAddressAsString()
								// +
								// " with sewnum "+rte.getSeqNum()+" route is even num");
								// routeSender.reset();
								routeTable.addRoutingEntry(rte);
								if (rte.getNumHops() < 2){
									changedConnected = true;
								} else {
									changedReachable = true;
								}
								// reBroadcast = true;
							} else {
								// System.out.println("Doing nada !! ");
							}
						} else if (existing.getSeqNum() == rte.getSeqNum()
								&& (existing.getNumHops() > rte.getNumHops())) {
							// System.out.println("Got from " +
							// rBroad.fromNetAddr.getAddressAsString()+" to "+rte.getDestination().getAddressAsString()
							// +
							// " with sewnum "+rte.getSeqNum()+" same seq lower hop");
							rte.setRouteChanged(true);
							routeTable.addRoutingEntry(rte);
							if (rte.getNumHops() < 2){
								changedConnected = true;
							} else {
								changedReachable = true;
							}
						} else {
							// System.out.println("Do nothing with Route");
						}
					}
				}
			}

			// reBroadcast message since there was a new route or a route with
			// an odd seq num or fewer hops
			if (reBroadcast) {
				routeSender = new RouteSender(this, routeTable);
				routeSender.start();
			}
			if (changedConnected) {
				notifyConnectedDevicesChanged();
			}
			if (changedReachable) {
				notifyReachableDevicesChanged();
			}

		} catch (Exception exception) {
			System.out.println("Exception in process : "
					+ exception.getMessage());
		}
	}

	/**
	 * Prints the routing table.
	 */
	public String printRouteTable() {
		Vector<RoutingTableEntryWrapper> v = routeTable.getAllRoutesAsVector();
		//Log.d(TAG, "Routing Table from route manager:");
		StringBuilder sb = new StringBuilder("RoutingTable for ");
		sb.append(ownAddress);
		sb.append("\n");
		
		for (int i = 0; i < v.size(); i++) {
			RoutingTableEntryWrapper rte = v.elementAt(i);
			sb.append(rte.toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}

	/**
	 * 
	 * @return all entries in the routing table (excluding our own)
	 */
	public String[] getAvailableDevices() {
		Vector<RoutingTableEntryWrapper> routes = routeTable
				.getRoutesAsVector();
		String[] connectedDevices = new String[routes.size() - 1];
		int j = 0; // index of connectedDevices
		for (int i = 0; i < routes.size(); i++) {
			String dest = ((RoutingTableEntryWrapper) routes.elementAt(i))
					.getDestination();
			if (dest.equals(getOwnAddress()))// own address
			{
			} else {
				connectedDevices[j] = dest;
				j++;
			}
		}
		return connectedDevices;
	}

	/**
	 * Method for sending unicast messages
	 * 
	 * @return
	 */
	public IFuture routePacket(DataPacket packet, String fromDevice) {
		// TODO: do some queueing here
		IFuture result = new Future();
		RoutingTableEntryWrapper routingEntry = routeTable.getRoutingEntry(packet.getDestination());
		
		if (routingEntry != null && routingEntry.isValid()) {
			String nextHop = routingEntry.getNextHop();
			packet.incHopCount();
			try {
				sendMessageToConnectedDevice(packet, nextHop);
				if (!nextHop.equals(packet.getDestination())) {
					Log.d(TAG, "Message sent to intermediate device: " + nextHop + " (dest: " + packet.getDestination() + ")");
				}
				result.setResult(BluetoothMessage.MESSAGE_SENT);
			} catch (MessageNotSendException e) {
				Log.e(TAG, "Could not send Message to: " + nextHop + ", see Stacktrace");
				e.printStackTrace();
				result.setException(e);
			}
		} else {
			Log.e(TAG, "Could not send Message to: " + packet.getDestination() + ", no route found");
			result.setException(new MessageNotSendException("Could not send Message to: " + packet.getDestination() + ", no route found"));
		}
		return result;
	}

	@Override
	// this method should not be needed
	// because every device is sending its own one-line-table around
	public void addConnectedDevice(String device) {
		RoutingTableEntryWrapper rte = new RoutingTableEntryWrapper(device,
				device, 1, CurrentInfo.incrementOwnSeqNum());
//		routeTable.addRoutingEntry(rte);
//		broadcastRouteTableMessage();
		
		Builder rtbuilder = RoutingTable.newBuilder();
		RoutingTableEntryWrapper rtew = new RoutingTableEntryWrapper();
		rtew.setDestination(device);
		rtew.setNextHop(device);
		rtew.setNumHops(0);
		RoutingTableEntryWrapper existing = routeTable.getRoutingEntry(device);
		
		rtew.setSeqNum(0);
		if (existing != null) {
			int seqNum = existing.getSeqNum();
			rtew.setSeqNum(seqNum % 2 == 0 ? seqNum +2 : seqNum + 1);
		} 
		
		rtbuilder.addEntry(rtew.build());
		jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder riBuilder = RoutingInformation.newBuilder();
		riBuilder.setFromAddress(device);
		riBuilder.setRoutingTable(rtbuilder);
		riBuilder.setRouteDownInformation(false);
		riBuilder.setType(RoutingType.DSDV);
		updateRoutingInformation(riBuilder.build());
	}

	@Override
	public void removeConnectedDevice(String device) {
		broadcastRouteDown(device);
	}

	@Override
	public void updateRoutingInformation(RoutingInformation ri) {
		if (ri.getType() == DsdvRouter.ROUTING_TYPE) {
			if (ri.getRouteDownInformation()) {
				processDSDVRouteDownBroadcast(ri);
			} else {
				processDSDVMsgBroadcast(ri);
			}
		}
	}
	
	@Override
	public Set<String> getReachableDeviceAddresses() {
		HashSet<String> result = new HashSet<String>();
		Vector<RoutingTableEntryWrapper> routes = routeTable
				.getRoutesAsVector();
		for (int i = 0; i < routes.size(); i++) {
			RoutingTableEntryWrapper entry = routes.elementAt(i);
			String dest = entry.getDestination();
			int hops = entry.getNumHops();
			if (hops > 1) {
				// reachable via another node
				result.add(dest);
			} else {
				// directly connected
			}
		}
		return result;
	}
	
	@Override
	public Set<String> getConnectedDeviceAddresses() {
		HashSet<String> result = new HashSet<String>();
		Vector<String> neighbours = routeTable.getNeighborAddresses();
		result.addAll(neighbours);
		return result;
	}

	@Override
	public void forceBroadcast() {
		broadcaster.broadcastRoute();
	}

	@Override
	public void forceDeleteStale() {
		// no action?
	}

	@Override
	public String toString() {
		return printRouteTable();
	}

}
