package jadex.android.bluetooth.device;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for Bluetooth Adapters
 * 
 * @author Julian Kalinowski
 * 
 */
public interface IBluetoothAdapter {

	/**
	 * Enum which specifies the State of the BluetoothAdapter
	 */
	public enum BluetoothState {
		/**
		 * Discovery is currently running
		 */
		discovery_started, 
		/**
		 * Discovery has just finished
		 */
		discovery_finished, 
		/**
		 * BluetoothAdapter is active 
		 */
		on, 
		/**
		 * BluetoothAdapter is turned off 
		 */
		off, 
		/**
		 * BluetoothAdapter is now turning on
		 */
		switching_on, 
		/**
		 * BluetoothAdapter is now turning off
		 */
		switching_off;
	}

	/**
	 * @return Local Bluetooth Address
	 */
	String getAddress();

	/**
	 * @return Set of bonded Bluetooth Devices
	 */
	Set<IBluetoothDevice> getBondedDevices();

	/**
	 * Checks whether this Adapter is enabled
	 * @return true, if this Adapter is enabled, else false
	 */
	boolean isEnabled();

	/**
	 * Turn this adapter on
	 */
	void enable();

	/**
	 * Start listening on an RFComm channel.
	 * @param serviceName Name of the service on this UUID
	 * @param uuid UUID of the service
	 * @return {@link IBluetoothServerSocket}
	 * @throws IOException if listening is not possible
	 */
	IBluetoothServerSocket listenUsingRfcommWithServiceRecord(
			String serviceName, UUID uuid) throws IOException;

	/**
	 * Checks whether this adapter is in discovery mode
	 * @return true, if this adapter is in Discovery mode right now
	 */
	boolean isDiscovering();

	/**
	 * Cancel any running Discovery
	 */
	void cancelDiscovery();

	/**
	 * Returns the {@link IBluetoothDevice} corresponding to the given Bluetooth Address
	 * @param address
	 * @return {@link IBluetoothDevice}
	 */
	IBluetoothDevice getRemoteDevice(String address);

	/**
	 * Initiates Bluetooth Discovery
	 */
	void startDiscovery();

	/**
	 * Check whether a given device is bonded with this Adapter
	 * @param device remote Device
	 * @return true, if device is bonded, else false
	 */
	boolean isDeviceBonded(IBluetoothDevice device);
}
