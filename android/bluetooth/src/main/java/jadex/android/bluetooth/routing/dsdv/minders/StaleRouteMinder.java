package jadex.android.bluetooth.routing.dsdv.minders;

import jadex.android.bluetooth.routing.dsdv.info.ConfigInfo;
import jadex.android.bluetooth.routing.dsdv.net.LocalRoutingTable;
import jadex.android.bluetooth.routing.dsdv.net.RoutingTableEntryWrapper;

import java.util.Vector;

/**
 * Go through the RouteTable and delete routes that are stale
 * 
 * @author Arnar
 */
public class StaleRouteMinder extends Thread {

	boolean abort = false;
	private LocalRoutingTable rt;
	private String ownAddress;

	public StaleRouteMinder(LocalRoutingTable rt, String ownAddress) {
		this.rt = rt;
		this.ownAddress = ownAddress;
	}

	/**
	 * go through all routes, if older then ConfigInfo.deleteRouteStaleRoute
	 * then delete from routing table
	 */
	public void run() {
		while (!abort) {
			try {
				Thread.sleep(ConfigInfo.deleteRouteSleepVal);
				Vector<RoutingTableEntryWrapper> v = rt.getRoutesAsVector();

				for (int i = 0; i < v.size(); i++) {
					RoutingTableEntryWrapper rte = (RoutingTableEntryWrapper) v
							.elementAt(i);
					long timeNow = System.currentTimeMillis();
					long lastRouteRenewTime = rte.getRouteCreationTime();

					// never delete self from routing table
					if (!(rte.getDestination().equals(ownAddress))
							&& timeNow - lastRouteRenewTime > ConfigInfo.deleteRouteStaleRoute) {
						rt.deleteRoutingEntry(rte.getDestination());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
