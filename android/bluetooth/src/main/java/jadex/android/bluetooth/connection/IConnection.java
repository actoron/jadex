package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.connection.ConnectionManager.ConnectionsListener;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.message.DataPacket;

import java.io.IOException;

/**
 * This interface represents a Bluetooth Connection.
 * Both In- and Outgoing Connections implement this interface.
 * @author Julian Kalinowski
 */
public interface IConnection {

	/**
	 * Is this Connection alive?
	 * @return true, if connection is alive and messages can be sent, else false.
	 */
	public boolean isAlive();

	/**
	 * Writes a {@link DataPacket} to the Output Stream.
	 * @param msg The {@link DataPacket} to be sent.
	 * @throws IOException if the Message couldn't be sent.
	 */
	public void write(DataPacket msg) throws IOException;

	/**
	 * Closes the Connection.
	 */
	public void close();

	/**
	 * Registers a {@link ConnectionsListener} which will be informed of 
	 * connection status changes.
	 * @param l the listener to be registered.
	 */
	public void addConnectionListener(IConnectionListener l);
	
	/**
	 * Removes a {@link ConnectionsListener}
	 * @param l the listener to be removed.
	 */
	public void removeConnectionListener(IConnectionListener l);
	
	/**
	 * Connects to the remote Device.
	 */
	public void connect();
	
	/**
	 * Returns the remote Device this Connection is associated to.
	 * @return {@link IBluetoothDevice}
	 */
	public IBluetoothDevice getRemoteDevice();
}