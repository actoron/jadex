package jadex.android.bluetooth.device.factory;

import jadex.android.bluetooth.device.IBluetoothDevice;

/**
 * @author Julian Kalinowski
 */
public interface IBluetoothDeviceFactory {
	/**
	 * Returns the instance of {@link IBluetoothDevice} specified by the given
	 * address.
	 * 
	 * @param address
	 * @return {@link IBluetoothDevice}
	 */
	IBluetoothDevice createBluetoothDevice(String address);
}