package jadex.android.bluetooth.routing.dsdv.minders;

import jadex.android.bluetooth.routing.dsdv.DsdvRouter;
import jadex.android.bluetooth.routing.dsdv.info.ConfigInfo;
import jadex.android.bluetooth.routing.dsdv.net.LocalRoutingTable;
import jadex.android.bluetooth.routing.dsdv.net.RoutingTableEntryWrapper;

import java.util.Vector;

/**
 * Periodically sends out messages to all neighbors containing the routing table
 * This only sends out incremental updates of the route table, i.e. routes that
 * are marked as having changed
 * 
 * @author Arnar
 */
public class PeriodicBroadcastMinder extends Thread {

	LocalRoutingTable rt;
	DsdvRouter rm;
	boolean abort = false;

	/**
	 * Class constructor
	 * 
	 * @param rm
	 *            route manager for broadcasting routing tables
	 * @param rt
	 *            routing table for getting all changed routing entries
	 */
	public PeriodicBroadcastMinder(DsdvRouter rm, LocalRoutingTable rt) {
		this.rm = rm;
		this.rt = rt;
	}

	public void run() {
		while (!abort) {
			try {
				Thread.sleep(ConfigInfo.periodicRouteBroadcastIncremental);
				// send out my routing table
				Vector<RoutingTableEntryWrapper> r = rt
						.getRoutesAsVectorChanged(false);
				if (r != null && r.size() > 0) {
					rm.BroadcastRouteTableMessageInstant(r);
				}

			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Kills the thread
	 */
	public synchronized void abort() {
		abort = true;
	}
}
