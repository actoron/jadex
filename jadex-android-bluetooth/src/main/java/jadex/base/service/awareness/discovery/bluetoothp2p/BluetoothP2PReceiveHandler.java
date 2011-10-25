package jadex.base.service.awareness.discovery.bluetoothp2p;

import jadex.android.bluetooth.service.IBTP2PAwarenessInfoCallback;
import jadex.android.bluetooth.util.Helper;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.ReceiveHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

/**
 * 
 */
public class BluetoothP2PReceiveHandler extends ReceiveHandler {
	/** The receive buffer. */
	protected byte[] buffer;

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
	 */
	@Override
	public Object[] receive() {

		try {
			Log.i(Helper.LOG_TAG,
					"BTP2PRecHandler: activating blocking queue...");
			byte[] take = awarenessQueue.take();
			Log.i(Helper.LOG_TAG, "BTP2PRecHandler: RECIEVED SOME MESSAGE");

			Object[] ret = new Object[3];
			// address:
			ret[0] = null;
			// port:
			ret[1] = 0;
			ret[2] = take;

			return ret;
		} catch (InterruptedException e) {
			return receive();
		}

		// try
		// {
		//
		// if(buffer==null)
		// {
		// // todo: max ip datagram length (is there a better way to determine
		// length?)
		// buffer = new byte[8192];
		// }
		//
		// final DatagramPacket pack = new DatagramPacket(buffer,
		// buffer.length);
		// getAgent().getSocket().receive(pack);
		// byte[] data = new byte[pack.getLength()];
		// System.arraycopy(buffer, 0, data, 0, pack.getLength());
		// ret = new Object[]{pack.getAddress(), new Integer(pack.getPort()),
		// data};
		// }
		// catch(Exception e)
		// {
		// //
		// getAgent().getMicroAgent().getLogger().warning("Message receival error: "+e);
		// }
		//
	}

	/**
	 * Get the agent.
	 */
	public BluetoothP2PDiscoveryAgent getAgent() {
		return (BluetoothP2PDiscoveryAgent) agent;
	}

	public void addReceivedAwarenessInfo(byte[] data) {
		Log.i(Helper.LOG_TAG, "AwarenessInfo received. Adding to Queue...");
		awarenessQueue.add(data);
		// synchronized (awarenessQueue) {
		// awarenessQueue.notifyAll();
		// }
	}
}
