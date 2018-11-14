package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothAdapter.BluetoothState;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothDevice.BluetoothBondState;

/**
 * Listener Interface
 * @author Julian Kalinowski
 *
 */
public interface IBluetoothStateListener {
	/**
	 * Called when the state of the BluetoothAdapter has changed.
	 * @param newState
	 * @param oldState
	 */
	void bluetoothStateChanged(BluetoothState newState, BluetoothState oldState);
	
	/**
	 * Called when a new bluetooth device was found.
	 * @param device
	 */
	void bluetoothDeviceFound(IBluetoothDevice device);
	
	/**
	 * Called when the bond state of a bluetooth device was changed.
	 * @param device
	 * @param newState
	 * @param oldState
	 */
	void bluetoothDeviceBondStateChanged(IBluetoothDevice device, BluetoothBondState newState, BluetoothBondState oldState);
}
