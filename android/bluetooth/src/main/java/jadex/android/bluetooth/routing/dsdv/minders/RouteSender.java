package jadex.android.bluetooth.routing.dsdv.minders;

import jadex.android.bluetooth.routing.dsdv.DsdvRouter;
import jadex.android.bluetooth.routing.dsdv.info.ConfigInfo;
import jadex.android.bluetooth.routing.dsdv.net.LocalRoutingTable;
import jadex.android.bluetooth.routing.dsdv.net.RoutingTableEntryWrapper;

import java.util.Vector;

/**
 * When activated this thread sleeps for the dampening time and then sends out the 
 * routing table.  
 * @author Arnar
 */
public class RouteSender extends Thread {

	LocalRoutingTable rt;
	DsdvRouter rm;
	boolean abort = false;
	private boolean reset = false;
	private boolean isRunning = false;

	public RouteSender(DsdvRouter rm, LocalRoutingTable rt) {
		this.rm = rm;
		this.rt = rt;
	}

	public void run() {
		isRunning = true;
		reset = false;
		try {
			Thread.sleep(ConfigInfo.changedRouteDampening); // sleep for
			// dampening time
			// and then send

			if (!reset) {
				// send out my routing table
				Vector<RoutingTableEntryWrapper> r = rt.getRoutesAsVectorChanged(true);
				rm.BroadcastRouteTableMessageInstant(r);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void reset() {
		this.reset = true;
	}

	public boolean isRunning() {
		return isRunning;
	}
}
