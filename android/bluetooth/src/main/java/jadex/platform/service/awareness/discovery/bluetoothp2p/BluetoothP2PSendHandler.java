package jadex.platform.service.awareness.discovery.bluetoothp2p;

import jadex.android.bluetooth.util.Helper;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.SendHandler;
import android.util.Log;

/**
 * Handle sending through the Bluetooth Connection Service
 */
public class BluetoothP2PSendHandler extends SendHandler {
	/**
	 * Create a new lease time handling object.
	 */
	public BluetoothP2PSendHandler(DiscoveryAgent agent) {
		super(agent);
		Log.d(Helper.LOG_TAG, "BluetoothP2PSendHandler created.");
	}

	/**
	 * Method to send messages via the Bluetooth Connection Service
	 */
	@Override
	public void send(AwarenessInfo info) {
		Log.d(Helper.LOG_TAG, "BluetoothP2PSendHandler: sending Awareness Info");
		byte[] data = DiscoveryAgent.encodeObject(info, getAgent().getMicroAgent().getClassLoader());
		getAgent().sendAwarenessInfo(data);
	}

	/**
	 * Get the agent.
	 */
	protected BluetoothP2PDiscoveryAgent getAgent() {
		return (BluetoothP2PDiscoveryAgent) agent;
	}
	
	
}