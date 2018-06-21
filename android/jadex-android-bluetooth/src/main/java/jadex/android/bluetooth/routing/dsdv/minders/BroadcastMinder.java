package jadex.android.bluetooth.routing.dsdv.minders;

import jadex.android.bluetooth.routing.dsdv.DsdvRouter;
import jadex.android.bluetooth.routing.dsdv.info.ConfigInfo;
import jadex.android.bluetooth.routing.dsdv.net.LocalRoutingTable;
import jadex.android.bluetooth.routing.dsdv.net.RoutingTableEntryWrapper;
import jadex.android.bluetooth.util.Helper;

import java.util.Vector;

import android.util.Log;

/**
 * Periodically sends out messages to all neighbors containing the routing table
 * This is the only one who can change the Sequence Number except for
 * RouteManager in the init method.
 * 
 * @author Arnar
 */
public class BroadcastMinder extends Thread {

	LocalRoutingTable rt;
	DsdvRouter rm;
	boolean abort = false;
	private String TAG = Helper.LOG_TAG;

	/**
	 * Kills the broadcast minder
	 */
	@Override
	public void destroy() {
		Log.d(TAG, "BroadcastMinder destroy called! ");
		super.destroy();
	}

	/**
	 * Class constructor
	 * 
	 * @param rm
	 *            routemanager for broadcasting routing tables
	 * @param rt
	 *            routing table for incrementing sequence numbers
	 */
	public BroadcastMinder(DsdvRouter rm, LocalRoutingTable rt) {
		this.rm = rm;
		this.rt = rt;
	}

	/**
	 * periodically sends out the routing table
	 */
	public void run() {
		while (!abort) {
			try {
				Thread.sleep(ConfigInfo.periodicRouteBroadcast);
				broadcastRoute();
				// logRouteTable();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Broadcast route table instantly.
	 */
	public void broadcastRoute() {
		// increase my seq num everytime I broadcast it
		RoutingTableEntryWrapper me = rt.getRoutingEntry(rm
				.getOwnAddress());
		if (me!= null) {
			me.increaseSeqNum();
		}

		// send out my routing table
		rm.broadcastRouteTableMessage();
	}

	/**
	 * kills the periodic sending of the routing table
	 */
	public synchronized void abort() {
		abort = true;
	}

	/**
	 * Prints the routing table
	 */
	@SuppressWarnings("unused")
	private void logRouteTable() {
		Vector<RoutingTableEntryWrapper> v = rt.getAllRoutesAsVector();
		Log.i(TAG, "**** Routing Table ****");
		for (int i = 0; i < v.size(); i++) {
			RoutingTableEntryWrapper rte = v.elementAt(i);
			Log.i(TAG, "**** Entry " + i + " " + rte.toString());
		}
	}

}
