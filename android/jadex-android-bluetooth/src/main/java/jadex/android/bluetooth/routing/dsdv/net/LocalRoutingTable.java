/**
 * This class is the Routing table itself, holds a collection of RouteingTableEntry objects.
 * It also manages all maintainance of the table
 */

package jadex.android.bluetooth.routing.dsdv.net;

import jadex.android.bluetooth.util.Helper;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;

/**
 * 
 * @author Arnar
 */
public class LocalRoutingTable {

	private Hashtable<String, RoutingTableEntryWrapper> rTable;
	private String[] neighbours;

	public final String TAG = Helper.LOG_TAG;
	public final String TAG2 = "LocalRoutingTable";
	private String ownAddress;

	public LocalRoutingTable(String ownAddress) {
		this.ownAddress = ownAddress;
		Log.d(TAG, "Created a LocalRoutingTable");
		rTable = new Hashtable<String, RoutingTableEntryWrapper>();
	}

	/**
	 * Adds a routing element to the routing table. This is used both for a new
	 * element as well as an update. It removes the old element if it exists and
	 * adds the new under the Destination address of the
	 * RoutingTableEntryWrapper
	 * 
	 * @param rte
	 *            the routing table entry to add
	 */
	public boolean addRoutingEntry(RoutingTableEntryWrapper rte) {

		try {
			String dest = rte.getDestination();
			rte.updateRouteCreationTime();
			synchronized (rTable) {
				rTable.put(dest, rte);
			}

		} catch (Exception exception) {
			return false;
		}

		return true;
	}

	/**
	 * Does not delete routes .. marks them as invalid
	 * 
	 * @param destination
	 *            the destination to mark as invalid
	 */
	public boolean removeRoutingEntry(String destination) {
		Log.d(TAG, TAG2 + " Removing entry : " + destination);
		try {
			synchronized (rTable) {
				RoutingTableEntryWrapper rte = getRoutingEntry(destination);
				rte.setRouteUnvalid(); // increase seq num by 1
				rte.setRouteChanged(true);
			}
		} catch (Exception exception) {
			return false;
		}
		return true;
	}

	/**
	 * Deletes the route permanently from the routing table
	 * 
	 * @param l
	 *            the destination to mark as invalid
	 */
	public void deleteRoutingEntry(String l) {
		System.out.println("Deleting entry :" + l);
		try {
			synchronized (rTable) {
				rTable.remove(l);
			}
		} catch (Exception exception) {
		}
	}

	/**
	 * Gets the routing table entry from an address
	 * 
	 * @param destination
	 *            the address to lookuo
	 * @return the routing table entry from the address
	 */
	public RoutingTableEntryWrapper getRoutingEntry(String destination) {
		RoutingTableEntryWrapper rte = rTable.get(destination);
		if (rte != null) {
			return rte;
		} else
			Log.d(TAG, "No entry found in the routing table returning null");
		return null;
	}

	/**
	 * Get all routes that are known to be valid
	 * 
	 * @return all routes that are known to be valid
	 */
	public Vector<RoutingTableEntryWrapper> getRoutesAsVector() {
		Vector<RoutingTableEntryWrapper> v = new Vector<RoutingTableEntryWrapper>();
		synchronized (rTable) {
			for (Enumeration<RoutingTableEntryWrapper> e = rTable.elements(); e
					.hasMoreElements();) {
				RoutingTableEntryWrapper rte = e.nextElement();
				if (rte.getSeqNum() % 2 == 0)
					v.addElement(rte);
			}
		}
		return v;
	}

	/**
	 * Get all neighbor RoutingEntries
	 * 
	 * @return all neightbors
	 */
	public Vector<RoutingTableEntryWrapper> getNeigborRoutesAsVector() {
		Vector<RoutingTableEntryWrapper> v = new Vector<RoutingTableEntryWrapper>();
		synchronized (rTable) {
			for (Enumeration<RoutingTableEntryWrapper> e = rTable.elements(); e
					.hasMoreElements();) {
				RoutingTableEntryWrapper rte = e.nextElement();
				if (rte.getNumHops() == 1)
					v.addElement(rte);
			}
		}
		return v;
	}

	/**
	 * Get all neighbors
	 * 
	 * @return all neightbors
	 */
	public Vector<String> getNeighborAddresses() {
		Vector<String> v = new Vector<String>();
		synchronized (rTable) {
			for (Enumeration<RoutingTableEntryWrapper> e = rTable.elements(); e
					.hasMoreElements();) {
				RoutingTableEntryWrapper rte = e.nextElement();
				if (rte.getNumHops() == 1 && rte.isValid())
					v.addElement(rte.getDestination());
			}
		}
		return v;
	}

	/**
	 * Returns all routes in the routing table as a vector
	 * 
	 * @return all routes
	 */
	public Vector<RoutingTableEntryWrapper> getAllRoutesAsVector() {
		Vector<RoutingTableEntryWrapper> v = new Vector<RoutingTableEntryWrapper>();
		synchronized (rTable) {
			for (Enumeration<RoutingTableEntryWrapper> e = rTable.elements(); e
					.hasMoreElements();) {
				RoutingTableEntryWrapper rte = e.nextElement();
				v.addElement(rte);
			}
		}
		return v;
	}

	/**
	 * Returns all routes that have changed
	 * 
	 * @param sendMe
	 *            adds your own address to the vector
	 * @return all routes that have changed
	 */
	public Vector<RoutingTableEntryWrapper> getRoutesAsVectorChanged(
			boolean sendMe) {
		Vector<RoutingTableEntryWrapper> v = new Vector<RoutingTableEntryWrapper>();
		synchronized (rTable) {
			if (sendMe)
				v.addElement(rTable.get(ownAddress));// always
														// send my
														// own info
			for (Enumeration<RoutingTableEntryWrapper> e = rTable.elements(); e
					.hasMoreElements();) {
				RoutingTableEntryWrapper rte = e.nextElement();
				if (rte.isRouteChanged()) {
					v.addElement(rte);
					rte.setRouteChanged(false); // reset route changed, update
												// has been sent
				}
			}
		}
		return v;
	}

	/**
	 * Gets all routes that use dest as the next hop. This is used to invalidate
	 * all routes going through a known bad node
	 * 
	 * @param dest
	 *            destination
	 * @return the routes to be marked as invalid
	 */
	public Vector<RoutingTableEntryWrapper> getRoutesAsVectorInvalid(String dest) {
		Vector<RoutingTableEntryWrapper> v = new Vector<RoutingTableEntryWrapper>();
		synchronized (rTable) {
			for (Enumeration<RoutingTableEntryWrapper> e = rTable.elements(); e
					.hasMoreElements();) {
				RoutingTableEntryWrapper rte = (RoutingTableEntryWrapper) e
						.nextElement();
				if (rte.getNextHop().equals(dest))// route is using dest as next
													// hop
				{
					rte.setRouteUnvalid();
					v.addElement(rte);
				}
			}
		}
		return v;
	}

	/**
	 * Goes through routing table, adding the entries in "neigbours" that have
	 * not been entered. Neighbours is the list of actual neighbours
	 * 
	 * @param routes
	 *            = list of entries currently marked as neighbours note that
	 *            this param is not the "new neigbours" they ares stored in the
	 *            neigbours array
	 */
	private void inputNewNeighbors(Vector<RoutingTableEntryWrapper> routes) {
		boolean exists = false;
		for (int j = 0; j < neighbours.length; j++) {
			exists = false;
			for (int i = 0; i < routes.size(); i++) {
				RoutingTableEntryWrapper rte = routes.elementAt(i);
				if (rte.getDestination() == neighbours[j]) {
					exists = true;
					if (!rte.isValid()) {
						rte.increaseSeqNum();
					}
				}
			}
			// if neighbor has left remove from routing table, but do not remove
			// own entry
			// because that is not in the neighbor table
			if (!exists) {
				String dest = neighbours[j];
				RoutingTableEntryWrapper rt = new RoutingTableEntryWrapper(
						dest, dest, 1, 0);
				addRoutingEntry(rt);
			}
		}
	}

	/**
	 * Goes through the new neighbor list and removes all entries from the route
	 * table if they are gone Could be rewritten for either speed or readability
	 */
	private synchronized void updateRouteTable() {
		Vector<RoutingTableEntryWrapper> routes = getNeigborRoutesAsVector();

		inputNewNeighbors(routes);
		Log.d(TAG, "Routingtable -> getNeigborRoutesAsVectore: route size = "
				+ routes.size());
		for (int i = 0; i < routes.size(); i++) {
			RoutingTableEntryWrapper rte = routes.elementAt(i);
			Log.d(TAG, "routes.elementAt(" + i + ") = " + routes.elementAt(i));
			// if not in new neighbor list then remove
			boolean exists = false; // assume neighbor has left
			for (int j = 0; j < neighbours.length; j++) {
				if (rte.getDestination() == neighbours[j]) {
					exists = true;
					Log.d(TAG, rte.getDestination()
							+ " Already found in table, not removed");
				}
			}
			// if neighbor has left remove from routing table, but do not remove
			// own entry
			// because that is not in the neighbor table
			if (!exists && rte.getNumHops() == 1) {
				removeRoutingEntry(rte.getDestination());
			}

		}
	}

	// /**
	// * Updates the routing table with new information
	// */
	// public synchronized void update(Object neigbourArray) {
	//
	// Log.i(TAG, " Routing Table: Update called"
	// + ((String[]) neigbourArray).length);
	// // NetworkAddress[] naUpdated = neigbourArray;
	//
	// // search for new devices if no connections exist
	// //
	// // TODO Might make maintainer do more active discovery if new
	// // neighbourArray is empty
	// // if(naUpdated.length == 0){
	// // new ConnectionMinder(rm,1l).start();
	// // }
	// exchangeArrays((String[]) neigbourArray);
	// updateRouteTable();
	// }

	// private synchronized void exchangeArrays(Object neigbourArray) {
	// neighbours = null;
	// neighbours = (String[]) neigbourArray;
	// }
	//
	// public synchronized String[] getNeigbourArray() {
	// if (neighbours == null || neighbours.length == 0)
	// return null;
	//
	// //TODO: Use clone instead!
	// String[] nArray = new String[neighbours.length];
	// System.arraycopy(neighbours, 0, nArray, 0, neighbours.length);
	// return nArray;
	// }
}
