package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.message.DataPacket;

/**
 * Listener Interface
 * @author Julian Kalinowski
 *
 */
public interface IConnectionListener {
	/**
	 * Called when a Connection changed its state
	 * @param connection
	 */
	void connectionStateChanged(IConnection connection);
	
	/**
	 * Called when a DataPacket was received.
	 * @param pkt the {@link DataPacket}
	 * @param fromDevice the Sender
	 * @param incomingConnection the Connection through which the Packet was received.
	 */
	void messageReceived(DataPacket pkt, IBluetoothDevice fromDevice, IConnection incomingConnection);
	
	/**
	 * Called when a DataPacket could not be sent.
	 * @param pkt
	 */
	void messageNotSent(DataPacket pkt);
}
