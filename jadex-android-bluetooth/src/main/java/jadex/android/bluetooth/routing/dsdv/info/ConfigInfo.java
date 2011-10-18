package jadex.android.bluetooth.routing.dsdv.info;

/**
 * This class holds static information
 */
public class ConfigInfo {

	// Parameters in their real data type. These parameters will be used
	// by the protocol handler when accessing. This is done to speed the
	// activities of the protocol handler.
//	public static long netAddressVal;

	// Used when creating a delete minder as the time to wait before
	// deleting a route
	public static int deleteRouteSleepVal = 20000;
	// FIXME: This was changed by us? We need maintenance protocols, this could
	// be a part of that
	public static int deleteRouteStaleRoute = 10000;

	// Used to indicate how often to broadcast routing table to neighbors
//	public static int periodicRouteBroadcast = 15000;
	public static int periodicRouteBroadcast = 5000;
	// Used to indicate how often to broadcast routing table to neighbors
	public static int periodicRouteBroadcastIncremental = 2000;

	// The time to wait before broadcasting an immediate message concerning a
	// new/changed route
	public static int changedRouteDampening = 2000;

	/**
	 * Constructor to create a default value filed ConfigInfo information
	 * object.
	 */
	public ConfigInfo() {
//		netAddressVal = dl.getNetworkAddress();

	}
}
