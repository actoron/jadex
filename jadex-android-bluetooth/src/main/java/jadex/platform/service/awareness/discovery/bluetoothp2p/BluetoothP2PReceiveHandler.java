package jadex.platform.service.awareness.discovery.bluetoothp2p;

import jadex.android.bluetooth.service.IBTP2PAwarenessInfoCallback;
import jadex.android.bluetooth.util.Helper;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.ReceiveHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

/**
 * Receive Handler for the Bluetooth Connection Service
 */
public class BluetoothP2PReceiveHandler extends ReceiveHandler {
	protected IBTP2PAwarenessInfoCallback awarenessCallback;
	private BlockingQueue<byte[]> awarenessQueue;

	/**
	 * Create a new receive handler.
	 */
	public BluetoothP2PReceiveHandler(DiscoveryAgent agent) {
		super(agent);
		awarenessQueue = new LinkedBlockingQueue<byte[]>();
	}

	/**
	 * Receive a packet.
	 * @throws InterruptedException 
	 */
	@Override
	public Object[] receive() throws InterruptedException {

		// blocks until Awarenessinfo is available:
		byte[] take = awarenessQueue.take();

		// create object to be handled by Jadex
		Object[] ret = new Object[3];
		// address:
		ret[0] = null;
		// port:
		ret[1] = 0;
		ret[2] = take;

		return ret;
	}

	/**
	 * Get the agent.
	 */
	public BluetoothP2PDiscoveryAgent getAgent() {
		return (BluetoothP2PDiscoveryAgent) agent;
	}

	/**
	 * Add Awarenessinfo to the Queue
	 * @param data
	 */
	public void addReceivedAwarenessInfo(byte[] data) {
		awarenessQueue.offer(data);
		Log.d(Helper.LOG_TAG, "AwarenessInfo received. Queue size: " + awarenessQueue.size());
	}
	
}
